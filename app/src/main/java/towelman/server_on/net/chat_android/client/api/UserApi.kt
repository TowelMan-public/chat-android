package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.UserEntity
import towelman.server_on.net.chat_android.client.exception.AlreadyUsedUserIdNameException
import towelman.server_on.net.chat_android.client.exception.NotFoundException

/**
 * ユーザー情報に関するAPIを呼び出すクラス
 */
class UserApi {
    var restTemplate = RestTemplate.getInstance()

    /**
     * ログインをするためのAPI
     * @param userIdName ユーザーID名
     * @param password パスワード
     * @return 認証用トークン
     * @throws NotFoundException
     */
    @Throws(NotFoundException::class)
    fun login(userIdName: String, password: String): String {
        val url = "$ROOT_URL/login"
        val dto = Dto(null,userIdName, password)
        return restTemplate.postForLogin(url, dto)
    }

    /**
     * ユーザー登録をするAPI
     * @param userName ユーザー名
     * @param userIdName ユーザーID名
     * @param password パスワード
     * @throws AlreadyUsedUserIdNameException
     */
    @Throws(AlreadyUsedUserIdNameException::class)
    fun insertUser(userName: String, userIdName: String, password: String) {
        val url = "$ROOT_URL/insert"
        val dto = Dto(userName, userIdName, password)
        restTemplate.post(url, dto)
    }

    /**
     * ユーザー情報を取得する
     * @param oauthToken 認証用トークン
     * @param userIdName ユーザーID名
     * @return ユーザー情報
     * @throws NotFoundException
     */
    @Throws(NotFoundException::class)
    fun getUser(oauthToken: String, userIdName: String): UserEntity {
        val url = "$ROOT_URL/get"
        val dto = Dto(null, userIdName, null)
        return restTemplate.getWhenLogined(oauthToken, url, dto)
    }

    /**
     * ユーザーID名を変更する
     * @param oauthToken 認証用トークン
     * @param userIdName ユーザーID名
     * @throws AlreadyUsedUserIdNameException
     */
    @Throws(AlreadyUsedUserIdNameException::class)
    fun updateUserIdName(oauthToken: String, userIdName: String) {
        val url = "$ROOT_URL/update/id-name"
        val dto = Dto(null, userIdName, null)
        restTemplate.postWhenLogined(oauthToken, url, dto)
    }

    /**
     * ユーザー名の変更
     * @param oauthToken 認証用トークン
     * @param userName ユーザー名
     */
    fun updateUserName(oauthToken: String, userName: String) {
        val url = "$ROOT_URL/update/name"
        val dto = Dto(userName, null, null)
        restTemplate.postWhenLogined(oauthToken, url, dto)
    }

    /**
     * パスワードの更新
     * @param oauthToken 認証用トークン
     * @param password パスワード
     */
    fun updatePassword(oauthToken: String, password: String) {
        val url = "$ROOT_URL/update/password"
        val dto = Dto(null, null, password)
        restTemplate.postWhenLogined(oauthToken, url, dto)
    }

    /**
     * 退会
     * @param oauthToken 認証用トークン
     */
    fun deleteUser(oauthToken: String) {
        val url = "$ROOT_URL/delete"
        restTemplate.postWhenLogined(oauthToken, url, null)
    }

    /**
     * ユーザー情報に関するapiのパラメターを送るためのDtoクラス
     */
    inner class Dto (val userName: String?, val userIdName: String?, val password: String?)

    companion object {
        private val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/user"
    }
}