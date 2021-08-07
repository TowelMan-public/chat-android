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
         * ユーザーの新規登録をする
         *
         * @param userIdName ユーザーID名
         * @param userName ユーザー名
         * @param password パスワード
         */
        fun signup(userIdName: String, userName: String, password: String) {
            UserApi.insertUser(userName, userIdName, password)
        }

        /**
         * ユーザーID名の変更をする
         *
         * @param oauthToken 認証用トークン
         * @param newUserIdName 新しいユーザーID名
         */
        fun changeUserIdName(oauthToken: String, newUserIdName: String){
            UserApi.updateUserIdName(oauthToken, newUserIdName)
        }

        /**
         * ユーザー名の変更をする
         *
         * @param oauthToken 認証用トークン
         * @param newUserName 新しいユーザー名
         */
        fun changeUserName(oauthToken: String, newUserName: String){
            UserApi.updateUserName(oauthToken, newUserName)
        }

        /**
         * パスワードの変更をする
         *
         * @param oauthToken 認証用トークン
         * @param newPassword 新しいパスワード
         */
        fun changePassword(oauthToken: String, newPassword: String){
            UserApi.updatePassword(oauthToken, newPassword)
        }

        /**
         * ユーザー名を取得する
         *
         * @param oauthToken 認証用トークン
         * @param userIdName ユーザーID名
         */
        fun getUserName(oauthToken: String, userIdName: String) = UserApi.getUser(oauthToken, userIdName).userName

        /**
         * 退会する
         *
         * @param oauthToken 認証用トークン
         */
        fun withdrawal(oauthToken: String){
            UserApi.deleteUser(oauthToken)
        }
    }
}