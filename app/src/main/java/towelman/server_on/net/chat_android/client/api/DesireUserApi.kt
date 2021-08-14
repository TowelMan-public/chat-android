package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.DesireHaveUserResponse
import towelman.server_on.net.chat_android.client.exception.NotFoundException

/**
 * 友達追加申請に関するAPIを呼び出すクラス<br>
 * このクラスは部品の集まりとして使う。
 */
class DesireUserApi
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/desire/user"
        private var restTemplate = RestTemplate.getInstance()

        /**
         * 友達追加申請リストを取得するAPI
         * @param oauthToken 認証用トークン
         * @return 友達追加申請リスト
         */
        fun getDesireUserList(oauthToken: String): List<DesireHaveUserResponse> {
            val url = "$ROOT_URL/gets"
            return restTemplate.getListWhenLogined(oauthToken, url, null, DesireHaveUserResponse::class.java)
        }

        /**
         * 友達追加申請を取得するAPi
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達のユーザーID名
         * @return 友達追加申請
         * @throws NotFoundException
         */
        @Throws(NotFoundException::class)
        fun getDesireUser(oauthToken: String, haveUserIdName: String): DesireHaveUserResponse {
            val url = "$ROOT_URL/get"
            val dto = Dto(haveUserIdName)
            return restTemplate.getWhenLogined(oauthToken, url, dto, DesireHaveUserResponse::class.java)
        }

        /**
         * 友達追加申請を断るAPI
         * @param oauthToken 認証用トークン
         * @param userIdName ユーザーID名
         * @throws NotFoundException
         */
        @Throws(NotFoundException::class)
        fun deleteDesireUser(oauthToken: String, userIdName: String) {
            val url = "$ROOT_URL/delete"
            val dto = Dto(userIdName)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * 友達追加申請を受けるAPI
         * @param oauthToken 認証用トークン
         * @param userIdName ユーザーID名
         * @throws NotFoundException
         */
        @Throws(NotFoundException::class)
        fun joinUser(oauthToken: String, userIdName: String) {
            val url = "$ROOT_URL/join"
            val dto = Dto(userIdName)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * 友達追加申請に関するAPIのパラメターを送るためのDtoクラス
         */
        class Dto(val userIdName: String?)
    }
}