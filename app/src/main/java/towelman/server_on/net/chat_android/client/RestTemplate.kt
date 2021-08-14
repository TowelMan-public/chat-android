package towelman.server_on.net.chat_android.client

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import okhttp3.*
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import java.io.IOException
import java.util.*
import kotlin.collections.LinkedHashMap


/**
 * 外部からAPIを呼び出すのを支援するクラス。
 */
class RestTemplate private constructor() {
    companion object {
        private const val OAUTH_HEADER_NAME = "X-AUTH-TOKEN"
        private val JSON_CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8")
        private val objectMapper = ObjectMapper()
        private val restTemplate = RestTemplate()
        private val client = OkHttpClient.Builder().build()
        private val restTemplateErrorHandler = RestTemplateErrorHandler.getInstance()

        /**
         * RestTemplateのインスタンスを返す
         * @return RestTemplateのインスタンス
         */
        fun getInstance() = restTemplate
    }

    /**
     * ログインされてるときに使うPOST実行メソッド
     * @param <Parameter> パラメターの型
     * @param oauthToken 認証用トークン
     * @param url URL
     * @param parameter リクエストパラメター
     */
    fun <Parameter>postWhenLogined(oauthToken: String, url: String, parameter: Parameter){
        val requestBody: RequestBody = RequestBody.create(JSON_CONTENT_TYPE, objectMapper.writeValueAsString(parameter))

        val request = Request.Builder()
            .url(url)
            .header(OAUTH_HEADER_NAME, oauthToken)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            restTemplateErrorHandler.checkErrorAndThrows(response.body()!!.string(), response.code())
        }catch (_: IOException){
            throw NetworkOfflineException()
        }
    }

    /**
     * ログインされているときに使うGET実行メソッド
     * @param <Response> レスポンスの型
     * @param <Parameter> パラメターの型
     * @param oauthToken 認証用トークン
     * @param url URL
     * @param parameter リクエストパラメター
     * @return レスポンス
     *
     * 参考: https://kazuhira-r.hatenablog.com/entry/2021/06/12/231251
     */
    fun <Parameter, Response>getWhenLogined(oauthToken: String, url: String, parameter: Parameter, responseClass: Class<Response>): Response {
        val request = Request.Builder()
                .url(url + createRequestParameterUrl(parameter))
                .header(OAUTH_HEADER_NAME, oauthToken)
                .get()
                .build()

        try {
            val response = client.newCall(request).execute()
            val responseJson = response.body()!!.string()
            restTemplateErrorHandler.checkErrorAndThrows(responseJson, response.code())
            return objectMapper
                    .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue<Response>(responseJson, responseClass)
        } catch (_: IOException){
            throw NetworkOfflineException()
        }
    }

    /**
     * ログインされているときに使うGET実行メソッド（List用）
     * @param <Response> レスポンスの型
     * @param <Parameter> パラメターの型
     * @param oauthToken 認証用トークン
     * @param url URL
     * @param parameter リクエストパラメター
     * @return レスポンス
     *
     * 参考: https://kazuhira-r.hatenablog.com/entry/2021/06/12/231251
     */
    fun <Parameter, ResponseInList>getListWhenLogined(oauthToken: String, url: String, parameter: Parameter, responseInListClass: Class<ResponseInList>): List<ResponseInList> {
        val mutableResponseList: MutableList<ResponseInList> = mutableListOf()

        val request = Request.Builder()
                .url(url + createRequestParameterUrl(parameter))
                .header(OAUTH_HEADER_NAME, oauthToken)
                .get()
                .build()

        try {
            val response = client.newCall(request).execute()
            val responseJson = response.body()!!.string()
            restTemplateErrorHandler.checkErrorAndThrows(responseJson, response.code())


            objectMapper.readTree(responseJson).forEach {
                val childJson = objectMapper.writeValueAsString(it)

                val childResponse =  objectMapper
                        .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(childJson, responseInListClass)
                
                mutableResponseList.add(childResponse)
            }
            return  mutableResponseList
        } catch (e: IOException){
            throw NetworkOfflineException()
        }
    }

    /**
     * ログインしていないときに使うPOST実行メソッド
     * @param <Parameter> パラメターの型
     * @param url URL
     * @param parameter リクエストパラメター
     */
    fun <Parameter>post(url: String, parameter: Parameter){
        val requestBody: RequestBody = RequestBody.create(JSON_CONTENT_TYPE, objectMapper.writeValueAsString(parameter))

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            restTemplateErrorHandler.checkErrorAndThrows(response.body()!!.string(), response.code())
        } catch (_: IOException){
            throw NetworkOfflineException()
        }
    }

    /**
     * ログインするとき専用のPOST実行メソッド
     * @param <Parameter> パラメターの型
     * @param url URL
     * @param parameter リクエストパラメター
     * @return 認証用トークン
     */
    fun <Parameter>postForLogin(url: String, parameter: Parameter): String{
        val requestBody: RequestBody = RequestBody.create(JSON_CONTENT_TYPE, objectMapper.writeValueAsString(parameter))

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseJson = response.body()!!.string()
            restTemplateErrorHandler.checkErrorAndThrows(responseJson, response.code())
            return responseJson
        } catch (_: IOException){
            throw NetworkOfflineException()
        }
    }

    /**
     * GETメソッド実行時に使うリクエストパラメタURL（リクエストURLの"?"以降の部分）作成
     * @param <Parameter> パラメターの型
     * @param parameter パラメター
     * @return リクエストパラメタURL
     */
    private fun <Parameter>createRequestParameterUrl(parameter: Parameter): String{
        val parameterJson = objectMapper.writeValueAsString(parameter)
        val parameterMap = objectMapper.readValue(parameterJson, object : TypeReference<Map<String?, String?>?>() {})

        val parameterUrlBuilder = StringBuilder()

        parameterMap?.forEach {
            if(it.value != null && !it.value.equals(""))
                parameterUrlBuilder.append("&${it.key}=${it.value}")
        }

        return if(parameterUrlBuilder.isEmpty())
            ""
        else
            "?" + parameterUrlBuilder.substring(1)
    }
}