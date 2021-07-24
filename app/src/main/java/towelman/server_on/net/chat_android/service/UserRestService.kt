package towelman.server_on.net.chat_android.service

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import towelman.server_on.net.chat_android.client.api.UserApi

class UserRestService {
    companion object {
        fun login(userIdName: String, password: String): String{
            return UserApi.login(userIdName, password)
        }

        fun signup(userIdName: String, userName: String, password: String){
            UserApi.insertUser(userName, userIdName, password)
        }
    }
}