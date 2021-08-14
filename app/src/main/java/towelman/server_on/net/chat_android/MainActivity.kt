package towelman.server_on.net.chat_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine
import towelman.server_on.net.chat_android.service.TalkRoomListNoticeJobService
import towelman.server_on.net.chat_android.updater.TalkRoomUpdater
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

        //AccountManager、及びその後の処理
        accountManager = AccountManagerAdapterForTowelman(this)
        if(!accountManager.haveAccount)
            transitionLoginAndSignnupActivity()
        else {
            setTalkRoomListUpdaterToUpdateManager()
            showHomeFragment()
        }
    }

    /**
     * このActivityが終了するときに実行される
     */
    override fun onDestroy() {
        UpdateManager.getInstance().deleteUpdaterAll()

        coroutineContext.cancel(null)

        //サービスの開始
        if(accountManager.haveAccount) {
            val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val componentName = ComponentName(this,
                    TalkRoomListNoticeJobService::class.java)
            val jobInfo = JobInfo.Builder(TALK_ROOM_LIST_NOTICE_JOB_SERVICE_ID, componentName)
                    .apply {
                        setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                        setPersisted(true)
                        setPeriodic(60000 * 1)
                        setRequiresCharging(false)
                    }.build()
            scheduler.schedule(jobInfo)
        }

        super.onDestroy()
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
            stopShowingProgressBar()
        } + ExceptionHandler.newIncense<HttpException>{ exception ->
            AlertDialog.Builder(this)
                    .setTitle("通信エラー")
                    .setMessage("予期しない通信エラーが発生しました。これまでに行われた操作は一部、あるいはすべてが無効、不正になっている可能性があります。" +
                            "このエラーが続く場合は開発者にこのエラーが発生したことを、状況等を細かく伝えてください。\n" +
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
     * ユーザー情報をこの機種から削除し、ログインとユーザーの新規登録
     * （この機種にこのアプリで使うアカウントが登録されるまで）を担当するActivityに遷移させる
     */
    fun finishForLogout(){
        accountManager.removeUserCache()
        transitionLoginAndSignnupActivity()
    }

    /**
     * トークルームリストのUpdaterを設定する
     */
    private fun setTalkRoomListUpdaterToUpdateManager(){
        val talkRoomUpdater = TalkRoomUpdater()

        talkRoomUpdater.updateDelegate = {
            TalkRoomListNoticeJobService.getTalkRoomList(this)
        }

        talkRoomUpdater.successDelegateList[this::class.java.name] = {
            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            TalkRoomListNoticeJobService.pushNotice(this, notificationManager, it!!)
        }

        talkRoomUpdater.beforeUpdateDelegate = {
            startShowingProgressBar()
        }

        talkRoomUpdater.afterUpdateDelegate = {
            stopShowingProgressBar()
        }

        showNotification(this, this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager, 1, "test")

        talkRoomUpdater.exceptionHandlingList = getExceptionHandlingListForCoroutine()

        val updateManager = UpdateManager.getInstance()
        updateManager.addUpdater(UpdateKeyConfig.TALK_ROOM_LIST, talkRoomUpdater)
        updateManager.setUpdateTimeSpan(UpdateKeyConfig.TALK_ROOM_LIST, 60000 * 5)
    }

    /**
     * 通知画面を表示する
     *
     * @param context 呼び出し元のコンテキスト
     * @param notificationManager 通知マネージャー
     * @param notificationId 通知ID
     * @param contentText 通知の内容
     */
    private fun showNotification(context: Context, notificationManager: NotificationManager, notificationId: Int, contentText: String){
            createNotificationChannel(notificationManager)
            //deleteNotification(notificationManager, notificationId)

            //通知の作成
            val notification = NotificationCompat.Builder(context, "towelman_chat_notification")
                    .setContentTitle("チャット♪")
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()

            //通知の登録
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, notification)
            }
    }

    /**
     * 通知チャンネルの作成
     * 作成する必要がなければ作成しない
     *
     * @param notificationManager 通知マネージャー
     */
    private fun createNotificationChannel(notificationManager: NotificationManager){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    notificationManager.getNotificationChannel("towelman_chat_notification") == null) {

                notificationManager.createNotificationChannel(
                        NotificationChannel("towelman_chat_notification",
                                "チャット♪",
                                NotificationManager.IMPORTANCE_LOW))
            }
    }

    /**
     * 通知の削除
     *
     * @param notificationManager 通知マネージャー
     * @param notificationId 通知ID
     */
    private fun deleteNotification(notificationManager: NotificationManager, notificationId: Int){
            notificationManager.cancel(notificationId)
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