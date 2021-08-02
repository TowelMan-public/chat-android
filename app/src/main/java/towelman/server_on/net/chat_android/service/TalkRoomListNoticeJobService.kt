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
import towelman.server_on.net.chat_android.R
import towelman.server_on.net.chat_android.account.AccountManagerAdapterForTowelman
import towelman.server_on.net.chat_android.client.api.*
import towelman.server_on.net.chat_android.client.exception.HttpException
import towelman.server_on.net.chat_android.client.exception.LoginException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.model.*
import towelman.server_on.net.chat_android.updater.TalkRoomUpdater
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager
import towelman.server_on.net.chat_android.updater.Updater

/**
 * トークルームに関する更新と通知をするサービスクラス
 */
class TalkRoomListNoticeJobService : JobService() {
    private val updateManager = UpdateManager.getInstance()

    /**
     * このサービスクラスが終わったときに呼ばれる
     */
    override fun onStopJob(params: JobParameters?): Boolean {
        jobFinished(params, false)
        return false
    }

    /**
     * 定期実行したい処理を書く
     */
    override fun onStartJob(params: JobParameters?): Boolean {
        if(!updateManager.isEnableUpdater(UpdateKeyConfig.TALK_ROOM_LIST))
            setUpdater()

        updateManager.getUpdater(UpdateKeyConfig.TALK_ROOM_LIST).runUpdate()
        return false
    }


    /**
     * Updaterをセットする
     */
    private fun setUpdater(){
        val accountManagerAdapter = AccountManagerAdapterForTowelman(this)

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        //定期実行したい更新の登録・作成
        updateManager.addUpdater(UpdateKeyConfig.TALK_ROOM_LIST, TalkRoomUpdater().apply {
            //更新処理の内容
            updateDelegate = {
                //初期化
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

                //return
                modelLists
            }

            //更新に成功したタイミングで実行したい処理
            successDelegateList[javaClass.name] = {
                //通知の合計数を取得する
                var noticeSum = 0
                it!![DialogueTalkRoomModel::javaClass.name]!!.forEach { model ->
                    noticeSum += model.noticeCount
                }
                it[GroupTalkRoomModel::javaClass.name]!!.forEach { model ->
                    noticeSum += model.noticeCount
                }

                if(noticeSum != 0)
                    showNotification(notificationManager, NORMAL_NOTIFICATION_ID, "あなた宛てに未読のチャットが${noticeSum}件あります")
                else
                    deleteNotification(notificationManager, NORMAL_NOTIFICATION_ID)
            }

            //エラー処理・通知
            exceptionHandlingList.add(
                    ExceptionHandler.newIncense<Exception> {
                        showNotification(notificationManager, WARNING_NOTIFICATION_ID, "予期しないエラーが発生しました。開発者にご報告ください。")
                        stop()
                    } + ExceptionHandler.newIncense<HttpException> {
                        showNotification(notificationManager, WARNING_NOTIFICATION_ID, "ネットワークに関わるエラーが発生しました。開発者にご報告ください")
                        stop()
                    } + ExceptionHandler.newIncense<NetworkOfflineException> {
                        //何もしない（ネットにつながってないだけだから）
                    } + ExceptionHandler.newIncense<LoginException> {
                        showNotification(notificationManager, WARNING_NOTIFICATION_ID, "本機種に登録されている本アプリのアカウントが無効になりました。再度ログインしてください。")
                        accountManagerAdapter.removeUserCache()
                        stop()
                    }
            )
        })
        updateManager.setUpdateTimeSpan(UpdateKeyConfig.TALK_ROOM_LIST, -1)
    }

    /**
     * このサービス自信を終了させる
     */
    private fun stop(){
        updateManager.deleteUpdater(UpdateKeyConfig.TALK_ROOM_LIST)
        stopSelf()
    }

    /**
     * 通帳画面を表示する
     *
     * @param notificationManager 通知マネージャー
     * @param notificationId 通知ID
     * @param contentText 通知の内容
     */
    private fun showNotification(notificationManager: NotificationManager, notificationId: Int, contentText: String){
        deleteNotification(notificationManager, notificationId)

        //通知の作成
        val notification = NotificationCompat.Builder(this, getString(R.string.notification_id))
                .setContentTitle(getString(R.string.notification_name))
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        //通知の登録
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notification)
        }

        //通知代入
        when(notificationId){
            NORMAL_NOTIFICATION_ID -> normalNotification = notification
            WARNING_NOTIFICATION_ID -> warningNotification = notification
        }
    }

    /**
     * 通知の削除
     *
     * @param notificationManager 通知マネージャー
     * @param notificationId 通知ID
     */
    private fun deleteNotification(notificationManager: NotificationManager, notificationId: Int){
        //削除する通知の取得
        val notification: Notification? = when(notificationId){
            NORMAL_NOTIFICATION_ID -> normalNotification
            WARNING_NOTIFICATION_ID -> warningNotification
            else -> null
        }

        //削除
        if(notification != null)
            notificationManager.notify(notificationId, notification)

        //後処理
        when(notificationId){
            NORMAL_NOTIFICATION_ID -> normalNotification = null
            WARNING_NOTIFICATION_ID -> warningNotification = null
        }
    }

    /**
     * 通知チャンネルの作成
     *
     * @param notificationManager 通知マネージャー
     */
    private fun createNotificationChannel(notificationManager: NotificationManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                    NotificationChannel(getString(R.string.notification_id),
                            getString(R.string.notification_name),
                            NotificationManager.IMPORTANCE_LOW))
        }
    }

    companion object{
        private const val NORMAL_NOTIFICATION_ID = 1
        private const val WARNING_NOTIFICATION_ID = 2

        @JvmStatic
        private var normalNotification: Notification? = null
        @JvmStatic
        private var warningNotification: Notification? = null
    }
}