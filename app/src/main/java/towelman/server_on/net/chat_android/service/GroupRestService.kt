package towelman.server_on.net.chat_android.service

import towelman.server_on.net.chat_android.client.api.GroupApi

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
    }
}