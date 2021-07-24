package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.UserInGroupResponse
import towelman.server_on.net.chat_android.client.exception.AlreadyInsertedGroupDesireException
import towelman.server_on.net.chat_android.client.exception.AlreadyInsertedGroupException
import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.client.exception.NotJoinGroupException


/**
 * グループ加入者に関するAPIを呼び出すクラス<br>
 * このクラスは部品の集まりとして使う。
 */
class UserInGroupApi
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/group/user"
        private var restTemplate = RestTemplate.getInstance()

        /**
         * グループにユーザーを加入させるAPI
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param userIdName ユーザーID名
         * @throws NotFoundException
         * @throws NotJoinGroupException
         * @throws AlreadyInsertedGroupDesireException
         * @throws AlreadyInsertedGroupException
         */
        @Throws(
                NotFoundException::class,
                NotJoinGroupException::class,
                AlreadyInsertedGroupDesireException::class,
                AlreadyInsertedGroupException::class
        )
        fun insertUserInGroup(oauthToken: String, groupTalkRoomId: Int, userIdName: String) {
            val url = "$ROOT_URL/insert"
            val dto = Dto(groupTalkRoomId, userIdName)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループ加入者リストの取得するAPI
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @return グループ加入者リスト
         * @throws NotJoinGroupException
         * @throws NotFoundException
         */
        @Throws(NotJoinGroupException::class, NotFoundException::class)
        fun getUsersInGroup(oauthToken: String, groupTalkRoomId: Int): List<UserInGroupResponse> {
            val url = "$ROOT_URL/gets"
            val dto = Dto(groupTalkRoomId, null)
            return restTemplate.getWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループ加入者をグループから抜けさせる
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param userIdName ユーザーID名
         * @throws NotFoundException
         * @throws NotJoinGroupException
         */
        @Throws(NotFoundException::class, NotJoinGroupException::class)
        fun deleteUserInGroup(oauthToken: String, groupTalkRoomId: Int, userIdName: String) {
            val url = "$ROOT_URL/delete"
            val dto = Dto(groupTalkRoomId, userIdName)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループから抜ける
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @throws NotJoinGroupException
         * @throws NotFoundException
         */
        @Throws(NotJoinGroupException::class, NotFoundException::class)
        fun exitGroup(oauthToken: String, groupTalkRoomId: Int) {
            val url = "$ROOT_URL/exit"
            val dto = Dto(groupTalkRoomId, null)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * グループ加入者に関するAPIのパラメターを送るためのDtoクラス
         */
        class Dto(val talkRoomId: Int?, val userIdName: String?)
    }
}