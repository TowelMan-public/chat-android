package towelman.server_on.net.chat_android.service

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
    }
}