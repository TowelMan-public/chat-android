package towelman.server_on.net.chat_android

import android.content.Intent
import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine


class MainActivity : AppCompatActivity() {
    val coroutineContext = Dispatchers.Main + SupervisorJob()
    val accountManager: AccountManagerAdapterForTowelman = AccountManagerAdapterForTowelman(this)
    private var exceptionHandlingListForCoroutine: ExceptionHandlingListForCoroutine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!accountManager.haveAccount)
            TransitionLoginAndSignnupActivity()

        exceptionHandlingListForCoroutine = createCoroutineExceptionHandler()
        showHomeFragment()
    }

    fun showValidationAlertDialogue(){
        AlertDialog.Builder(this)
                .setTitle("入力チェックエラー")
                .setMessage("入力に不備があります。もう一度ご確認のほどよろしくお願いします。")
                .setPositiveButton(OK, null)
                .show()
    }

    fun getExceptionHandlingListForCoroutine(): ExceptionHandlingListForCoroutine = exceptionHandlingListForCoroutine!!

    private fun TransitionLoginAndSignnupActivity(){
        val intent = Intent(application, LoginAndSignupActivity::class.java)
        startActivity(intent)
    }

    private fun showHomeFragment(){
        //TODO
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, HomeFragment.newInstance())
        transaction.commit()
    }

    private fun createCoroutineExceptionHandler() : ExceptionHandlingListForCoroutine {
        val handlerList = ExceptionHandlingListForCoroutine()

        handlerList += ExceptionHandler<Exception>{
            AlertDialog.Builder(this)
                .setTitle("重大なエラー")
                .setMessage("予期しないエラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                        "開発者にこのエラーが発生したことを、状況等を細かく伝えてください。")
                .setPositiveButton(OK, null)
                .show()
        } + ExceptionHandler<HttpException>{ exception ->
            AlertDialog.Builder(this)
                .setTitle("通信エラー")
                .setMessage("予期しない通信エラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                        "開発者にこのエラーが発生したことを、状況等を細かく伝えてください。\n" +
                        "ステータスコード: ${exception.httpStatusCode}")
                .setPositiveButton(OK, null)
                .show()
        } + ExceptionHandler<NetworkOfflineException>{
            AlertDialog.Builder(this)
                .setTitle("通信エラー")
                .setMessage("ネットーワークで障害が発生しました。このエラーは本体がネットにつながってないと発生することがあります")
                .setPositiveButton(OK, null)
                .show()
        }

        return handlerList
    }
}