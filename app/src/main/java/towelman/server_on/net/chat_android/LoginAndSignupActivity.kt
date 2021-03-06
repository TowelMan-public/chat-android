package towelman.server_on.net.chat_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine

/**
 * ログインとユーザーの新規登録（この機種にこのアプリで使うアカウントが登録されるまで）を担当するActivity
 */
class LoginAndSignupActivity : AppCompatActivity() {
    /**
     * このActivityの管理下で使うコルーチンのためのコンテキスト
     */
    val coroutineContext = Dispatchers.Main + SupervisorJob()

    /**
     * このActivityの管理下で使うAccountManagerAdapter
     */
    lateinit var accountManager: AccountManagerAdapterForTowelman

    /**
     * このActivityが生成されたときの処理
     *
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_signup)

        accountManager = AccountManagerAdapterForTowelman(this)
        showLoginFragment()
    }

    /**
     * ログイン用のFragmentを表示させる
     */
    fun showLoginFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, LoginFragment.newInstance())
        transaction.commit()
    }

    /**
     * ユーザーの新規登録用のFragmentを表示させる
     */
    fun showSignupFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, SignupFragment.newInstance())
        transaction.commit()
    }

    /**
     * バリデーションチェックで引っかかった時のアラートメッセージを表示する
     */
    fun showValidationAlertDialogue(){
        AlertDialog.Builder(this)
                .setTitle("入力チェックエラー")
                .setMessage("入力に不備があります。もう一度ご確認のほどよろしくお願いします。")
                //.setPositiveButton(DateTimePatternGenerator.PatternInfo.OK, null)
                .show()
    }

    /**
     * グルグルの表示を始める
     */
    fun startShowingProgressBar(){
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
    }

    /**
     * グルグルの表示を止める
     */
    fun stopShowingProgressBar(){
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
    }

    /**
     * 処理をするときに行うべき必要最低限な、最終的な例外ハンドラーの集まりを取得する。<br>
     * 大体これで取得した奴に、取得した側で必要な例外ハンドラーを追加して使う。
     */
    fun getCoroutineExceptionHandler(): ExceptionHandlingListForCoroutine {
        val handlerList = ExceptionHandlingListForCoroutine()

        handlerList += ExceptionHandler.newIncense<Exception>{
            AlertDialog.Builder(this)
                    .setTitle("重大なエラー")
                    .setMessage("予期しないエラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                            "開発者にこのエラーが発生したことを、状況等を細かく伝えてください。")
                    .show()
            stopShowingProgressBar()
        } + ExceptionHandler.newIncense<HttpException>{ exception ->
            AlertDialog.Builder(this)
                    .setTitle("通信エラー")
                    .setMessage("予期しない通信エラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                            "開発者にこのエラーが発生したことを、状況等を細かく伝えてください。\n" +
                            "ステータスコード: ${exception.httpStatusCode}")
                    .show()
            stopShowingProgressBar()
        } + ExceptionHandler.newIncense<NetworkOfflineException>{
            AlertDialog.Builder(this)
                    .setTitle("通信エラー")
                    .setMessage("ネットーワークで障害が発生しました。このエラーは本体がネットにつながってないと発生することがあります")
                    .show()
            stopShowingProgressBar()
        }

        return handlerList
    }

    /**
     * メインのActivityに画面遷移させるためのクラス
     */
    fun transitionMainActivity(){
        val intent = Intent(application, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}