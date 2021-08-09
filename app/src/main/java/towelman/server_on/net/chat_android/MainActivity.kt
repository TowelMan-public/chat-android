package towelman.server_on.net.chat_android

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine
import towelman.server_on.net.chat_android.service.TalkRoomListNoticeJobService
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager

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
    lateinit  var accountManager: AccountManagerAdapterForTowelman

    /**
     * このActivityが生成されたときの処理
     *
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //サービスの終了
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(TALK_ROOM_LIST_NOTICE_JOB_SERVICE_ID)

        //サービスの開始
        UpdateManager.getInstance().deleteUpdater(UpdateKeyConfig.TALK_ROOM_LIST)
        val componentName = ComponentName(this,
            TalkRoomListNoticeJobService::class.java)
        val jobInfo = JobInfo.Builder(TALK_ROOM_LIST_NOTICE_JOB_SERVICE_ID, componentName)
            .apply {
                setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                setPersisted(true)
                setPeriodic(10000)
                setRequiresCharging(false)
            }.build()
        scheduler.schedule(jobInfo)

        //AccountManager、及びその後の処理
        accountManager = AccountManagerAdapterForTowelman(this)
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
                //.setPositiveButton(OK, null)
                .show()
    }

    /**
     * 処理をするときに行うべき必要最低限な、最終的な例外ハンドラーの集まりを取得する。<br>
     * 大体これで取得した奴に、取得した側で必要な例外ハンドラーを追加して使う。
     */
    fun getExceptionHandlingListForCoroutine(): ExceptionHandlingListForCoroutine {
        val handlerList = ExceptionHandlingListForCoroutine()

        handlerList += ExceptionHandler.newIncense<Exception>{
            AlertDialog.Builder(this)
                    .setTitle("重大なエラー")
                    .setMessage("予期しないエラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                            "このエラーが続く場合は開発者にこのエラーが発生したことを、状況等を細かく伝えてください。")
                    .show()
        } + ExceptionHandler.newIncense<HttpException>{ exception ->
            AlertDialog.Builder(this)
                    .setTitle("通信エラー")
                    .setMessage("予期しない通信エラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                            "このエラーが続く場合は開発者にこのエラーが発生したことを、状況等を細かく伝えてください。\n" +
                            "ステータスコード: ${exception.httpStatusCode}")
                    .show()
        } + ExceptionHandler.newIncense<NetworkOfflineException>{
            AlertDialog.Builder(this)
                    .setTitle("通信エラー")
                    .setMessage("ネットーワークで障害が発生しました。このエラーは本体がネットにつながってないと発生することがあります")
                    .show()
        }

        return handlerList
    }

    /**
     * ユーザー情報をこの機種から削除し、ログインとユーザーの新規登録
     * （この機種にこのアプリで使うアカウントが登録されるまで）を担当するActivityに遷移させる
     */
    fun finishForLogout(){
        accountManager.removeUserCache()
        transitionLoginAndSignnupActivity()
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

    companion object{
        const val TALK_ROOM_LIST_NOTICE_JOB_SERVICE_ID = 3
    }
}