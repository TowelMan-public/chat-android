package towelman.server_on.net.chat_android.client

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import towelman.server_on.net.chat_android.client.exception.*
import java.io.IOException

/**
 * RestTemplateのエラーハンドラー
 * @see RestTemplate
 */
class RestTemplateErrorHandler private constructor() {
    companion object{
        const val HTTP_UNAUTHORIZED = 401

        private val restTemplateErrorHandler = RestTemplateErrorHandler()
        private val objectMapper = ObjectMapper()

        /**
         * RestTemplateErrorHandlerのインスタンスを取得する
         * @return RestTemplateErrorHandlerのインスタンス
         */
        fun getInstance() = restTemplateErrorHandler
    }

    /**
     * レスポンスからエラーがあるかを調べ、あれば適宜例外を投げる
     * @param responseJson レスポンスのJSON文字列
     * @param httpStatusCode httpステータス
     */
    fun checkErrorAndThrows(responseJson: String, httpStatusCode: Int){
        if(hasError(httpStatusCode)){
            val errorResponse = parseJson(responseJson)
            throwsException(errorResponse, httpStatusCode)
        }
    }

    /**
     * エラーがあるかを判定する
     * @param httpStatusCode httpステータス
     * @return エラーがあればtrue, 無ければfalse
     */
    private fun hasError(httpStatusCode: Int) = httpStatusCode >= 400

    /**
     * JSON文字列からエラーレスポンスを取得する
     * @param responseJson JSON文字列
     * @return エラーレスポンス
     */
    private fun parseJson(responseJson: String): ErrorResponse {
        return try {
            objectMapper
                    .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
                    .readValue(responseJson, object : TypeReference<ErrorResponse>() {})
        }
        catch (_: IOException){
            ErrorResponse()
        }
    }

    /**
     * 適宜例外を投げる（実際にエラーをハンドリングする）
     * @param errorResponse エラーレスポンス
     * @param httpStatusCode httpステータス
     */
    private fun throwsException(errorResponse: ErrorResponse, httpStatusCode: Int){
        when (errorResponse.errorCode) {
            "NotFoundException" -> throw NotFoundException(errorResponse.message)
            "LoginException" -> throw LoginException(errorResponse.message)
            "AlreadyHaveUserException" -> throw AlreadyHaveUserException()
            "AlreadyInsertedGroupDesireException" -> throw AlreadyInsertedGroupDesireException()
            "AlreadyInsertedGroupException" -> throw AlreadyInsertedGroupException()
            "AlreadyUsedUserIdNameException" -> throw AlreadyUsedUserIdNameException()
            "NotHaveUserException" -> throw NotHaveUserException()
            "NotInsertedGroupDesireException" -> throw NotInsertedGroupDesireException()
            "NotJoinGroupException" -> throw NotJoinGroupException()
            "BadRequestFormException" -> throw BadRequestFormException(errorResponse.message)
            else ->{
                if (httpStatusCode == HTTP_UNAUTHORIZED)
                    throw InvalidLoginException(errorResponse.message)
                else
                    throw HttpException(errorResponse.message, httpStatusCode)
            }
        }
    }

    /**
     * エラーレスポンス
     */
    class ErrorResponse{
        var errorCode: String = ""
        val message: String = ""
    }
}