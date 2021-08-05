package towelman.server_on.net.chat_android.service

import towelman.server_on.net.chat_android.client.api.DesireGroupApi
import towelman.server_on.net.chat_android.client.api.GroupApi
import towelman.server_on.net.chat_android.client.api.UserInGroupApi

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
    }
}