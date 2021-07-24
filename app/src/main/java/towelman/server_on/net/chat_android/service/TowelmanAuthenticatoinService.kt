package towelman.server_on.net.chat_android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import towelman.server_on.net.chat_android.account.TowelmanAuthenticatoin

class TowelmanAuthenticatoinService: Service() {
    private var mAuthenticator: TowelmanAuthenticatoin? = null

    override fun onCreate() {
        super.onCreate()
        mAuthenticator = TowelmanAuthenticatoin(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mAuthenticator!!.getIBinder()
    }
}