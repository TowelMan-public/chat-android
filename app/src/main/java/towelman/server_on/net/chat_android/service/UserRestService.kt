package towelman.server_on.net.chat_android.service

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import towelman.server_on.net.chat_android.client.api.UserApi

/**
 * APIを使う、ユーザーに関する処理を書くところ<br>
 * このクラスは部品の集まりとして使う。
 */
class UserRestService
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        /**
         * ログインし、認証用トークンを取得する
         *
         * @param userIdName ユーザーID名
         * @param password パスワード
         * @return 認証用トークン
         */
        fun login(userIdName: String, password: String): String {
            return UserApi.login(userIdName, password)
        }

        /**
         * ユーザーの新規登録をするクラス
         *
         * @param userIdName ユーザーID
         * @param userName ユーザー名
         * @param password パスワード
         */
        fun signup(userIdName: String, userName: String, password: String) {
            UserApi.insertUser(userName, userIdName, password)
        }
    }
}