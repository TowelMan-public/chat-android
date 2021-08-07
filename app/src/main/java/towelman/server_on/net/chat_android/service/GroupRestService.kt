package towelman.server_on.net.chat_android.service

import towelman.server_on.net.chat_android.client.api.DesireGroupApi
import towelman.server_on.net.chat_android.client.api.GroupApi
import towelman.server_on.net.chat_android.client.api.UserInGroupApi
import towelman.server_on.net.chat_android.client.entity.UserInGroupResponse

/**
 * APIを使う、グループに関する処理を書くところ<br>
 * このクラスは部品の集まりとして使う。
 */
class GroupRestService
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor(){
    companion object {
        /**
         * グループ作成
         *
         * @param oauthToken 認証用トークン
         * @param groupName グループ名
         */
        fun makeGroup(oauthToken: String, groupName: String) {
            GroupApi.insertGroup(oauthToken, groupName)
        }

        /**
         * グループから抜ける
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         */
        fun brock(oauthToken: String, groupTalkRoomId: Int){
            UserInGroupApi.exitGroup(oauthToken, groupTalkRoomId)
        }

        /**
         * グループからの勧誘を断る
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         */
        fun brockDesire(oauthToken: String, groupTalkRoomId: Int){
            DesireGroupApi.deleteDesireGroup(oauthToken, groupTalkRoomId)
        }

        /**
         * グループからの勧誘を受け入れる
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         */
        fun acceptDesire(oauthToken: String, groupTalkRoomId: Int){
            DesireGroupApi.joinGroup(oauthToken, groupTalkRoomId)
        }

        /**
         * グループ名の変更
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         */
        fun getGroupName(oauthToken: String, groupTalkRoomId: Int) = GroupApi.getGroup(oauthToken, groupTalkRoomId).groupName

        /**
         * グループ加入者一覧を取得する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         */
        fun getUserInGroupList(oauthToken: String, groupTalkRoomId: Int): List<UserInGroupResponse> {
            return UserInGroupApi.getUsersInGroup(oauthToken, groupTalkRoomId)
        }

        /**
         * グループ加入者を脱退させる
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param userIdName ユーザーID名
         */
        fun deleteUserInGroup(oauthToken: String, groupTalkRoomId: Int, userIdName: String){
            UserInGroupApi.deleteUserInGroup(oauthToken, groupTalkRoomId, userIdName)
        }

        /**
         * グループに勧誘する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param newGroupName 新しいグループ名
         */
        fun changeGroupName(oauthToken: String, groupTalkRoomId: Int, newGroupName: String){
            GroupApi.updateGroupName(oauthToken, groupTalkRoomId, newGroupName)
        }

        /**
         * グループを削除する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         */
        fun deleteGroup(oauthToken: String, groupTalkRoomId: Int){
            GroupApi.deleteGroup(oauthToken, groupTalkRoomId)
        }

        /**
         * グループから脱退する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         */
        fun exitGroup(oauthToken: String, groupTalkRoomId: Int){
            UserInGroupApi.exitGroup(oauthToken, groupTalkRoomId)
        }

        /**
         * グループに勧誘する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param userIdName ユーザーID名
         */
        fun invitationUserToGroup(oauthToken: String, groupTalkRoomId: Int, userIdName: String){
            UserInGroupApi.insertUserInGroup(oauthToken, groupTalkRoomId, userIdName)
        }
    }
}