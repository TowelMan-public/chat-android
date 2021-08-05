package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.TalkResponse
import towelman.server_on.net.chat_android.client.exception.BadRequestFormException
import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.client.exception.NotHaveUserException
import towelman.server_on.net.chat_android.client.exception.NotJoinGroupException

/**
 * グループトーク単体に関するAPIを呼び出すクラス<br>
 * このクラスは部品の集まりとして使う。
 */
class GroupTalkApi
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/group/talk"
        private var restTemplate = RestTemplate.getInstance()

        /**
         * グループトークの作成をするAPI
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param talkContentText トークの内容
         * @throws NotFoundException
         * @throws NotHaveUserException
         */
        @Throws(NotFoundException::class, NotJoinGroupException::class)
        fun insertTalk(oauthToken: String, groupTalkRoomId: Int, talkContentText: String) {
            val url = "$ROOT_URL/insert"
            val dto = Dto(groupTalkRoomId, talkContentText, null)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループトークの取得をするAPI
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param talkIndex　トークインデックス
         * @return 友達トーク
         * @throws NotFoundException
         * @throws NotHaveUserException
         */
        @Throws(NotFoundException::class, NotJoinGroupException::class)
        fun getTalk(oauthToken: String, groupTalkRoomId: Int, talkIndex: Int): TalkResponse {
            val url = "$ROOT_URL/get"
            val dto = Dto(groupTalkRoomId, null, talkIndex)
            return restTemplate.getWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループトークの更新をするAPI
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param talkIndex トークインデックス
         * @param talkContentText トークテキスト
         * @throws NotFoundException
         * @throws NotHaveUserException
         * @throws BadRequestFormException
         */
        @Throws(NotFoundException::class, NotJoinGroupException::class, BadRequestFormException::class)
        fun updateTalk(oauthToken: String, groupTalkRoomId: Int, talkIndex: Int, talkContentText: String) {
            val url = "$ROOT_URL/update"
            val dto = Dto(groupTalkRoomId, talkContentText, talkIndex)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループトークの削除をするAPI
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param talkIndex トークインデックス
         * @throws NotFoundException
         * @throws NotHaveUserException
         * @throws BadRequestFormException
         */
        @Throws(NotFoundException::class, NotJoinGroupException::class, BadRequestFormException::class)
        fun deleteTalk(oauthToken: String, groupTalkRoomId: Int, talkIndex: Int) {
            val url = "$ROOT_URL/delete"
            val dto = Dto(groupTalkRoomId, null, talkIndex)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループトーク単体に関するAPIのパラメターを送るためのDtoクラス
         */
        class Dto(val groupTalkRoomId: Int?, val talkContentText: String?, val talkIndex: Int?)
    }
}