package towelman.server_on.net.chat_android

import android.app.Activity
import android.icu.text.DateTimePatternGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine

class LoginAndSignupActivity : AppCompatActivity() {
    val coroutineContext = Dispatchers.Main + SupervisorJob()
    val accountManager:AccountManagerAdapterForTowelman = AccountManagerAdapterForTowelman(this)
    private var exceptionHandlingListForCoroutine: ExceptionHandlingListForCoroutine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_signup)
        exceptionHandlingListForCoroutine = createExceptionHandlingListForCoroutine()

        showLoginFragment()
    }

    fun showLoginFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, LoginFragment.newInstance())
        transaction.commit()
    }

    fun showSignupFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, SignupFragment.newInstance())
        transaction.commit()
    }

    fun showValidationAlertDialogue(){
        AlertDialog.Builder(this)
                .setTitle("入力チェックエラー")
                .setMessage("入力に不備があります。もう一度ご確認のほどよろしくお願いします。")
                .setPositiveButton(DateTimePatternGenerator.PatternInfo.OK, null)
                .show()
    }

    fun getCoroutineExceptionHandler(): ExceptionHandlingListForCoroutine = exceptionHandlingListForCoroutine!!

    private fun createExceptionHandlingListForCoroutine() :ExceptionHandlingListForCoroutine{
        val handlerList = ExceptionHandlingListForCoroutine()

        handlerList += ExceptionHandler<Exception>{
            AlertDialog.Builder(this)
                    .setTitle("重大なエラー")
                    .setMessage("予期しないエラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                            "開発者にこのエラーが発生したことを、状況等を細かく伝えてください。")
                    .setPositiveButton(DateTimePatternGenerator.PatternInfo.OK, null)
                    .show()
        } + ExceptionHandler<HttpException>{exception ->
            AlertDialog.Builder(this)
                    .setTitle("通信エラー")
                    .setMessage("予期しない通信エラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                            "開発者にこのエラーが発生したことを、状況等を細かく伝えてください。\n" +
                            "ステータスコード: ${exception.httpStatusCode}")
                    .setPositiveButton(DateTimePatternGenerator.PatternInfo.OK, null)
                    .show()
        } + ExceptionHandler<NetworkOfflineException>{
            AlertDialog.Builder(this)
                .setTitle("通信エラー")
                .setMessage("ネットーワークで障害が発生しました。このエラーは本体がネットにつながってないと発生することがあります")
                .setPositiveButton(DateTimePatternGenerator.PatternInfo.OK, null)
                .show()
        }

        return handlerList
    }
}