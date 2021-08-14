package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.GroupTalkRoomResponse
import towelman.server_on.net.chat_android.client.entity.TalkResponse
import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.client.exception.NotJoinGroupException


/**
 * グループに関するAPIを呼び出すクラス<br>
 * このクラスは部品の集まりとして使う。
 */
class GroupApi
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/group"
        private var restTemplate = RestTemplate.getInstance()

        /**
         * グループ情報を取得するAPI
         * @param oauthToken 認証用トークン
         * @param talkRoomId グループトークルームID
         * @return グループ情報
         * @throws NotJoinGroupException
         * @throws NotFoundException
         */
        @Throws(NotJoinGroupException::class, NotFoundException::class)
        fun getGroup(oauthToken: String, talkRoomId: Int): GroupTalkRoomResponse {
            val url = "$ROOT_URL/get"
            val dto = Dto(talkRoomId, null, null, null)
            return restTemplate.getWhenLogined(oauthToken, url, dto, GroupTalkRoomResponse::class.java)
        }

        /**
         * 加入してるグループリストの取得をするAPI
         * @param oauthToken 認証用トークン
         * @return グループリスト
         */
        fun getGroups(oauthToken: String): List<GroupTalkRoomResponse> {
            val url = "$ROOT_URL/gets"
            return restTemplate.getListWhenLogined(oauthToken, url, null, GroupTalkRoomResponse::class.java)
        }

        /**
         * グループ名を変更するAPI
         * @param oauthToken 認証用トークン
         * @param talkRoomId グループトークルームID
         * @param groupName グループ名
         * @throws NotJoinGroupException
         * @throws NotFoundException
         */
        @Throws(NotJoinGroupException::class, NotFoundException::class)
        fun updateGroupName(oauthToken: String, talkRoomId: Int, groupName: String) {
            val url = "$ROOT_URL/update/name"
            val dto = Dto(talkRoomId, groupName, null, null)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループトークリストの取得をするAPI
         * @param oauthToken 認証用トークン
         * @param talkRoomId グループトークルームID
         * @param startIndex 最初のトークインデックス
         * @param maxSize 最大件数
         * @return グループトークリスト
         * @throws NotFoundException
         * @throws NotJoinGroupException
         */
        @Throws(NotFoundException::class, NotJoinGroupException::class)
        fun getGroupTalks(oauthToken: String, talkRoomId: Int, startIndex: Int, maxSize: Int): List<TalkResponse> {
            val url = "$ROOT_URL/gets/talks"
            val dto = Dto(talkRoomId, null, startIndex, maxSize)
            return restTemplate.getListWhenLogined(oauthToken, url, dto, TalkResponse::class.java)
        }

        /**
         * グループの削除
         * @param oauthToken 認証用トークン
         * @param talkRoomId グループトークルームID
         * @throws NotJoinGroupException
         * @throws NotFoundException
         */
        @Throws(NotJoinGroupException::class, NotFoundException::class)
        fun deleteGroup(oauthToken: String, talkRoomId: Int) {
            val url = "$ROOT_URL/delete"
            val dto = Dto(talkRoomId, null, null, null)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループの作成
         * @param oauthToken 認証用トークン
         * @param groupName グループ名
         */
        fun insertGroup(oauthToken: String, groupName: String) {
            val url = "$ROOT_URL/insert"
            val dto = Dto(null, groupName, null, null)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループに関するAPIのパラメターを送るためのDtoクラス
         */
        class Dto(val talkRoomId: Int?, val groupName: String?, val startIndex: Int?, val maxSize: Int?)
    }
}