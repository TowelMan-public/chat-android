package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.HaveUserResponse
import towelman.server_on.net.chat_android.client.exception.AlreadyHaveUserException
import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.client.exception.NotHaveUserException


/**
 * ユーザーが持っている友達に関するAPIを呼び出すクラス
 */
class UserInDialogueApi {
    var restTemplate = RestTemplate.getInstance()

    /**
     * 友達リストの取得をするAPI
     * @param oauthToken 認証用トークン
     * @return 友達リスト
     */
    fun getUserInDialogueList(oauthToken: String): List<HaveUserResponse> {
        val url = "$ROOT_URL/gets"
        return restTemplate.getWhenLogined(oauthToken, url, null)
    }

    /**
     * 友達を取得する
     * @param oauthToken 認証用トークン
     * @param haveUserIdName 友達のユーザーID名
     * @return 友達
     * @throws NotFoundException
     * @throws NotHaveUserException
     */
    @Throws(NotFoundException::class, NotHaveUserException::class)
    fun getUserInDialogue(oauthToken: String, haveUserIdName: String): HaveUserResponse {
        val url = "$ROOT_URL/get"
        val dto = Dto(haveUserIdName)
        return restTemplate.getWhenLogined(oauthToken, url, dto)
    }

    /**
     * 友達追加をするAPI
     * @param oauthToken 認証用トークン
     * @param userIdName ユーザーID名
     * @throws NotFoundException
     * @throws AlreadyHaveUserException
     */
    @Throws(NotFoundException::class, AlreadyHaveUserException::class)
    fun insertUserInDialogue(oauthToken: String, userIdName: String) {
        val url = "$ROOT_URL/insert"
        val dto = Dto(userIdName)
        restTemplate.postWhenLogined(oauthToken, url, dto)
    }

    /**
     * 友達を削除する
     * @param oauthToken 認証用トークン
     * @param userIdName ユーザーID名
     * @throws NotFoundException
     * @throws NotHaveUserException
     */
    @Throws(NotFoundException::class, NotHaveUserException::class)
    fun deleteUserInDialogue(oauthToken: String, userIdName: String) {
        val url = "$ROOT_URL/delete"
        val dto = Dto(userIdName)
        restTemplate.postWhenLogined(oauthToken, url, dto)
    }

    /**
     * 友達追加申請に関するAPIのパラメターを送るためのDtoクラス
     */
    inner class Dto(val userIdName: String?)

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/diarogue/user"
    }
}