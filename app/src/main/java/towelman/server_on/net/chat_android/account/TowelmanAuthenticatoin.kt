package towelman.server_on.net.chat_android.account

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import towelman.server_on.net.chat_android.LoginAndSignupActivity
import towelman.server_on.net.chat_android.client.api.UserApi

/**
 * このアプリで使うAccountManagerで使う処理定義クラス。今回は役目を果たしてない、形だけのクラスである。
 */
class TowelmanAuthenticatoin(context: Context): AbstractAccountAuthenticator(context) {

    @Throws(NetworkErrorException::class)
    override fun addAccount(response: AccountAuthenticatorResponse?, accountType: String?,
                            authTokenType: String?, requiredFeatures: Array<String?>?, options: Bundle?): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account,
                              authTokenType: String?, options: Bundle?): Bundle? {
        return null
    }

    override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(response: AccountAuthenticatorResponse?, account: Account?,
                                    options: Bundle?): Bundle? {
        return null
    }

    override fun getAuthTokenLabel(authTokenType: String?): String? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(response: AccountAuthenticatorResponse?, account: Account?,
                                   authTokenType: String?, options: Bundle?): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?,
                             features: Array<String?>?): Bundle? {
        return null
    }

    companion object {
        private const val ACCOUNT_TYPE = "net.server-on.towelman.chat"
    }
}