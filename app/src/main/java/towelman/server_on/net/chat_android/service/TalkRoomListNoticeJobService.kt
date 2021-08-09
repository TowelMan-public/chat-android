package towelman.server_on.net.chat_android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import towelman.server_on.net.chat_android.R
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.api.*
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.LoginException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine
import towelman.server_on.net.chat_android.model.*
import towelman.server_on.net.chat_android.updater.TalkRoomUpdater
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager
import towelman.server_on.net.chat_android.updater.Updater

/**
 * トークルームに関する更新と通知をするサービスクラス
 */
class TalkRoomListNoticeJobService : JobService() {
    /**
     * 実行中のジョブを止める必要がある場合に呼ばれる
     *
     * @param params このJobServiceの情報
     */
    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    /**
     * 定期実行したい処理を書く
     *
     *  @param params このJobServiceの情報
     */
    override fun onStartJob(params: JobParameters?): Boolean {
        val context = this
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch(getCoroutineExceptionHandler(notificationManager)) {
            val allTalkRoomList = withContext(Dispatchers.Default) {
                getTalkRoomList(context)
            }
            pushNotice(context, notificationManager, allTalkRoomList)
        }

        return false
    }

    /**
     * 例外ハンドラーを作成する
     *
     * @param notificationManager 通知マネージャー
     * @return コルーチン用の例外ハンドラー
     */
    private fun getCoroutineExceptionHandler(notificationManager: NotificationManager): CoroutineExceptionHandler{
        val accountManagerAdapter = AccountManagerAdapterForTowelman(this)

        val exceptionHandlingListForCoroutine = ExceptionHandlingListForCoroutine()
        exceptionHandlingListForCoroutine.add(
            ExceptionHandler.newIncense<Exception> {
                showNotification(this, notificationManager, WARNING_NOTIFICATION_ID, "予期しないエラーが発生しました。開発者にご報告ください。")
            } + ExceptionHandler.newIncense<HttpException> {
                showNotification(this, notificationManager, WARNING_NOTIFICATION_ID, "ネットワークに関わるエラーが発生しました。開発者にご報告ください")
            } + ExceptionHandler.newIncense<NetworkOfflineException> {
                //何もしない（ネットにつながってないだけだから）
            } + ExceptionHandler.newIncense<LoginException> {
                showNotification(this, notificationManager, WARNING_NOTIFICATION_ID, "本機種に登録されている本アプリのアカウントが無効になりました。再度ログインしてください。")
                accountManagerAdapter.removeUserCache()
            }
        )

        return exceptionHandlingListForCoroutine.createCoroutineExceptionHandler()
    }

    companion object{
        private const val NORMAL_NOTIFICATION_ID = 1
        private const val WARNING_NOTIFICATION_ID = 2

        private const val NOTIFICATION_ID = "towelman_chat_notification"
        private const val NOTIFICATION_NAME = "チャット♪"

        /**
         *　全てのトークルームリストを取得する
         *
         * @param context 呼び出し元のコンテキスト
         * @return 全てのトークルームリスト（最初の[]にDialogueTalkRoomModel等のクラスの「javaClass.name」を指定して使う）
         */
        fun getTalkRoomList(context: Context): MutableMap<String, MutableList<TalkRoomModel>>{
            val accountManagerAdapter = AccountManagerAdapterForTowelman(context)

            val modelLists: MutableMap<String, MutableList<TalkRoomModel>> = mutableMapOf()
            modelLists[DialogueTalkRoomModel::javaClass.name] = mutableListOf()
            modelLists[GroupTalkRoomModel::javaClass.name] = mutableListOf()
            modelLists[DesireDialogueTalkRoomModel::javaClass.name] = mutableListOf()
            modelLists[DesireGroupTalkRoomModel::javaClass.name] = mutableListOf()

            //友達トークルームリスト取得
            UserInDialogueApi.getUserInDialogueList(accountManagerAdapter.getOauthToken()).forEach {
                modelLists[DialogueTalkRoomModel::javaClass.name]!!
                        .add(DialogueTalkRoomModel(it.haveUserName, it.myLastTalkIndex, it.haveUserIdName,
                                it.talkLastIndex - it.myLastTalkIndex))
            }

            //グループトークルームリスト取得
            GroupApi.getGroups(accountManagerAdapter.getOauthToken()).forEach{
                modelLists[GroupTalkRoomModel::javaClass.name]!!
                        .add(GroupTalkRoomModel(it.groupName, it.userLastTalkIndex, it.talkRoomId,
                                it.groupLastTalkIndex - it.userLastTalkIndex))
            }

            //友達追加申請者とのトークルームリスト
            DesireUserApi.getDesireUserList(accountManagerAdapter.getOauthToken()).forEach {
                modelLists[DesireDialogueTalkRoomModel::javaClass.name]!!
                        .add(DesireDialogueTalkRoomModel(it.haveUserName, it.lastTalkIndex, it.haveUserIdName,0))
            }

            //勧誘されているグループのトークルームリスト取得
            DesireGroupApi.getDesireUserList(accountManagerAdapter.getOauthToken()).forEach {
                modelLists[DesireGroupTalkRoomModel::javaClass.name]!!
                        .add(DesireGroupTalkRoomModel(it.groupName, it.lastTalkIndex, it.talkRoomId,0))
            }

            return modelLists
        }

        /**
         * 全てのトークルームリストから通知するべきものを抽出し、それを通知する
         *
         * @param context 呼び出し元のコンテキスト
         * @param notificationManager 通知マネージャー
         * @param allTalkRoomList 全てのトークルームリスト
         */
        fun pushNotice(context: Context, notificationManager: NotificationManager,  allTalkRoomList: MutableMap<String, MutableList<TalkRoomModel>>){
            //通知の合計数を取得する
            var noticeSum = 0
            allTalkRoomList[DialogueTalkRoomModel::javaClass.name]!!.forEach { model ->
                noticeSum += model.noticeCount
            }
            allTalkRoomList[GroupTalkRoomModel::javaClass.name]!!.forEach { model ->
                noticeSum += model.noticeCount
            }

            if(noticeSum != 0)
                showNotification(context, notificationManager, NORMAL_NOTIFICATION_ID, "あなた宛てに未読のチャットが${noticeSum}件あります")
            else
                deleteNotification(notificationManager, NORMAL_NOTIFICATION_ID)
        }

        /**
         * 通帳画面を表示する
         *
         * @param context 呼び出し元のコンテキスト
         * @param notificationManager 通知マネージャー
         * @param notificationId 通知ID
         * @param contentText 通知の内容
         */
        private fun showNotification(context: Context, notificationManager: NotificationManager, notificationId: Int, contentText: String){
            createNotificationChannel(notificationManager)
            deleteNotification(notificationManager, notificationId)

            //通知の作成
            val notification = NotificationCompat.Builder(context, NOTIFICATION_ID)
                    .setContentTitle(NOTIFICATION_NAME)
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
                    notificationManager.getNotificationChannel(NOTIFICATION_ID) != null) {

                notificationManager.createNotificationChannel(
                        NotificationChannel(NOTIFICATION_ID,
                                NOTIFICATION_NAME,
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
    }
}