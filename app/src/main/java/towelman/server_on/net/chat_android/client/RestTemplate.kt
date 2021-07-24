package towelman.server_on.net.chat_android.client

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import java.io.IOException

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
            restTemplateErrorHandler.checkErrorAndThrows(response.toString(), response.code())
        }catch (_ : IOException){
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
     */
    fun <Parameter, Response>getWhenLogined(oauthToken: String, url: String, parameter: Parameter): Response {
        val request = Request.Builder()
            .url(url + createRequestParameterUrl(parameter))
            .header(OAUTH_HEADER_NAME, oauthToken)
            .get()
            .build()

        try {
            val response = client.newCall(request).execute()
            restTemplateErrorHandler.checkErrorAndThrows(response.toString(), response.code())
            return objectMapper.readValue<Response>(response.toString(),object : TypeReference<Response>() {})
        } catch (_ : IOException){
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
            restTemplateErrorHandler.checkErrorAndThrows(response.toString(), response.code())
        } catch (_ : IOException){
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
            restTemplateErrorHandler.checkErrorAndThrows(response.toString(), response.code())
            return response.toString()
        } catch (_ : IOException){
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