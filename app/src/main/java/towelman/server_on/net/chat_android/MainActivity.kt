package towelman.server_on.net.chat_android

import android.content.Intent
import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine

/**
 * このアプリのメインで使うActivity
 */
class MainActivity : AppCompatActivity() {
    /**
     * このActivityの管理下で使うコルーチンのためのコンテキスト
     */
    val coroutineContext = Dispatchers.Main + SupervisorJob()

    /**
     * このActivityの管理下で使うAccountManagerAdapter
     */
    val accountManager: AccountManagerAdapterForTowelman = AccountManagerAdapterForTowelman(this)

    /**
     * このActivityが生成されたときの処理
     *
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!accountManager.haveAccount)
            transitionLoginAndSignnupActivity()
        else
            showHomeFragment()
    }

    /**
     * バリデーションチェックで引っかかった時のアラートメッセージを表示する
     */
    fun showValidationAlertDialogue(){
        AlertDialog.Builder(this)
                .setTitle("入力チェックエラー")
                .setMessage("入力に不備があります。もう一度ご確認のほどよろしくお願いします。")
                .setPositiveButton(OK, null)
                .show()
    }

    /**
     * 処理をするときに行うべき必要最低限な、最終的な例外ハンドラーの集まりを取得する。<br>
     * 大体これで取得した奴に、取得した側で必要な例外ハンドラーを追加して使う。
     */
    fun getExceptionHandlingListForCoroutine(): ExceptionHandlingListForCoroutine {
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

    /**
     * ログインとユーザーの新規登録を担当するのActivityに画面遷移させるためのクラス
     */
    private fun transitionLoginAndSignnupActivity(){
        val intent = Intent(application, LoginAndSignupActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * このActivityの、メインとなるFragmentの表示
     */
    private fun showHomeFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, HomeFragment.newInstance())
        transaction.commit()
    }
}