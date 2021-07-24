package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.client.exception.NotInsertedGroupDesireException
import towelman.server_on.net.chat_android.client.entity.DesireUserInGroupResponse
import towelman.server_on.net.chat_android.client.RestTemplate

/**
 * グループ加入してほしい申請に関するAPIを呼び出すクラス<br>
 * このクラスは部品の集まりとして使う。
 */
class DesireGroupApi
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/desire/group"

        private var restTemplate = RestTemplate.getInstance()

        /**
         * グループに加入してほしい申請リストの取得
         * @param oauthToken 認証用トークン
         * @return グループに加入してほしい申請リスト
         */
        fun getDesireUserList(oauthToken: String): List<DesireUserInGroupResponse> {
            val url = "$ROOT_URL/gets"
            return restTemplate.getWhenLogined(oauthToken, url, null)
        }

        /**
         * グループに加入してほしい申請を取得する
         * @param oauthToken 認証用トークン
         * @param talkRoomId グループトークルーム
         * @return グループに加入してほしい申請
         * @throws NotFoundException
         */
        @Throws(NotFoundException::class)
        fun getDesireUser(oauthToken: String, talkRoomId: Int): DesireUserInGroupResponse {
            val url = "$ROOT_URL/get"
            val dto = Dto(talkRoomId)
            return restTemplate.getWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループ加入してほしい申請を断る
         * @param oauthToken 認証用トークン
         * @param talkRoomId グループトークルームID
         * @throws NotFoundException
         * @throws NotInsertedGroupDesireException
         */
        @Throws(NotFoundException::class, NotInsertedGroupDesireException::class)
        fun deleteDesireGroup(oauthToken: String, talkRoomId: Int) {
            val url = "$ROOT_URL/delete"
            val dto = Dto(talkRoomId)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループ加入してほしい申請を受ける
         * @param oauthToken 認証用トークン
         * @param talkRoomId グループトークルームID
         * @throws NotFoundException
         * @throws NotInsertedGroupDesireException
         */
        @Throws(NotFoundException::class, NotInsertedGroupDesireException::class)
        fun joinGroup(oauthToken: String, talkRoomId: Int) {
            val url = "$ROOT_URL/join"
            val dto = Dto(talkRoomId)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * 友達追加申請に関するAPIのパラメターを送るためのDtoクラス
         */
        class Dto(val talkRoomId: Int?)
    }
}