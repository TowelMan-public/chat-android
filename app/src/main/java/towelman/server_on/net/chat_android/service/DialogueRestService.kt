package towelman.server_on.net.chat_android.service

import towelman.server_on.net.chat_android.client.api.DesireUserApi
import towelman.server_on.net.chat_android.client.api.DialogueApi
import towelman.server_on.net.chat_android.client.api.GroupApi
import towelman.server_on.net.chat_android.client.api.UserInDialogueApi

/**
 * APIを使う、グループに関する処理を書くところ<br>
 * このクラスは部品の集まりとして使う。
 */
class DialogueRestService
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor(){
    companion object {
        /**
         * 友達登録をする
         *
         * @param oauthToken 認証用トークン
         * @param userIdName 友達登録したいユーザーのID
         */
        fun addDialogue(oauthToken: String, userIdName: String){
            UserInDialogueApi.insertUserInDialogue(oauthToken, userIdName)
        }

        /**
         * 友達をブロックする
         *
         * @param oauthToken 認証用トークン
         * @param userIdName 友達登録したいユーザーのID
         */
        fun brock(oauthToken: String, userIdName: String){
            UserInDialogueApi.deleteUserInDialogue(oauthToken, userIdName)
        }

        /**
         * 友達追加申請を断る
         *
         * @param oauthToken 認証用トークン
         * @param userIdName 友達登録したいユーザーのID
         */
        fun brockDesire(oauthToken: String, userIdName: String){
            DesireUserApi.deleteDesireUser(oauthToken, userIdName)
        }

        /**
         * 友達追加申請を受け入れる
         *
         * @param oauthToken 認証用トークン
         * @param userIdName 友達登録したいユーザーのID
         */
        fun acceptDesire(oauthToken: String, userIdName: String){
            DesireUserApi.joinUser(oauthToken, userIdName)
        }
    }
}