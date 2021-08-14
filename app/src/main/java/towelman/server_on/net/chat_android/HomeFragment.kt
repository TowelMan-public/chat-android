package towelman.server_on.net.chat_android

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import towelman.server_on.net.chat_android.model.DialogueTalkRoomModel
import towelman.server_on.net.chat_android.model.GroupTalkRoomModel
import towelman.server_on.net.chat_android.model.TalkRoomModel
import towelman.server_on.net.chat_android.updater.TalkRoomUpdater
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager
import java.lang.Byte.decode
import java.lang.Integer.decode
import java.lang.Long.decode
import java.lang.Short.decode

/**
 * ホーム画面の全体のFragment<br>
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var thisView: View
    
    /**
     * このFragmentのUI等を生成するときの処理
     *
     * @param inflater 配置等を管理するやつ
     * @param container このFragmentを配置する場所
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * このFragmentのViewたちが生成されたときときの処理
     * このFragmentのViewたちの設定等
     *
     * @param view このFragmentのViewたち
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view

        setSuccessDelegateToUpdater()
        setConfigToAllButton()
    }

    /**
     * このFragmentが終了するときの処理
     */
    override fun onStop() {
        super.onStop()

        val updateManager = UpdateManager.getInstance()
        if(updateManager.isEnableUpdater(UpdateKeyConfig.TALK_ROOM_LIST)) {
            val talkRoomListUpdater = updateManager.getUpdater(UpdateKeyConfig.TALK_ROOM_LIST) as TalkRoomUpdater
            talkRoomListUpdater.successDelegateList.remove(this::class.java.name)
        }
    }

    /**
     * トークリストフラグメントを表示させる
     *
     * @param talkRoomModel 表示させたいトークルームのモデルクラス
     */
    fun showTalkListInTalkRoom(talkRoomModel: TalkRoomModel){
        showFragment(TalkListInTalkRoomFragment.newInstance(talkRoomModel))
    }

    /**
     * グループ詳細フラグメントを表示させる
     *
     * @param groupTalkRoomModel グループトークルームモデル
     */
    fun showGroupDetailsFragment(groupTalkRoomModel: GroupTalkRoomModel){
        showFragment(GroupDetailsFragment.newInstance(groupTalkRoomModel.groupTalkRoomId))
    }

    /**
     * 退会確認フラグメントを表示させる
     */
    fun showWithdrawalFragment(){
        showFragment(WithdrawalFragment.newInstance())
    }

    /**
     * ユーザー設定画面を表示する
     */
    fun showUserConfigFragment(){
        showFragment(UserConfigFragment.newInstance())
    }

    /**
     * 子フラグメントを閉じる
     */
    fun closeChildFragment(){
        val fragment = childFragmentManager.findFragmentById(R.id.container)

        if(fragment != null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }

        UpdateManager.getInstance().getUpdater(UpdateKeyConfig.TALK_ROOM_LIST).runUpdate()
    }

    /**
     * トークルームリストを更新するUpdaterの、成功時の処理にこのフラグメントでやりたいことをセットし、更新処理を開始させる
     */
    private fun setSuccessDelegateToUpdater(){
        val talkRoomUpdater = UpdateManager.getInstance().getUpdater(UpdateKeyConfig.TALK_ROOM_LIST) as TalkRoomUpdater
        talkRoomUpdater.successDelegateList[this::class.java.name] = {
            val talkRoomListHasNoticeButtonWhenHasNoting = thisView.findViewById<ImageButton>(R.id.talkRoomListHasNoticeButtonWhenHasNoting)
            val talkRoomListHasNoticeButtonWhenHasMoreZero = thisView.findViewById<ImageButton>(R.id.talkRoomListHasNoticeButtonWhenHasMoreZero)
            var noticeSum = 0

            it!![DialogueTalkRoomModel::class.java.name]!!.forEach { model ->
                noticeSum += model.noticeCount
            }
            it[GroupTalkRoomModel::class.java.name]!!.forEach { model ->
                noticeSum += model.noticeCount
            }

            if(noticeSum == 0){
                talkRoomListHasNoticeButtonWhenHasNoting.visibility = View.VISIBLE
                talkRoomListHasNoticeButtonWhenHasMoreZero.visibility = View.GONE
            }else{
                talkRoomListHasNoticeButtonWhenHasNoting.visibility = View.GONE
                talkRoomListHasNoticeButtonWhenHasMoreZero.visibility = View.VISIBLE
            }
        }

        talkRoomUpdater.runUpdate()
    }

    /**
     * このフラグメントの全てのボタンの設定をする
     */
    private fun setConfigToAllButton(){
        thisView.findViewById<ImageButton>(R.id.talkRoomListButton).setOnClickListener {
            showTalkRoomListFragment()
        }

        thisView.findViewById<ImageButton>(R.id.talkRoomListHasNoticeButtonWhenHasNoting).setOnClickListener {
            showTalkRoomListHasNoticeButtonFragment()
        }

        thisView.findViewById<ImageButton>(R.id.talkRoomListHasNoticeButtonWhenHasMoreZero).setOnClickListener {
            showTalkRoomListHasNoticeButtonFragment()
        }

        thisView.findViewById<ImageButton>(R.id.addDialogueButton).setOnClickListener {
            showAddDialogueFragment()
        }

        thisView.findViewById<ImageButton>(R.id.makeGroupButton).setOnClickListener {
            showMakeGroupFragment()
        }

        thisView.findViewById<ImageButton>(R.id.userConfigButton).setOnClickListener {
            showUserConfigFragment()
        }
    }

    /**
     * トークルームリストフラグメントを表示させる
     */
    private fun showTalkRoomListFragment(){
        showFragment(TalkRoomListFragment.newInstance())
    }

    /**
     * グループ作成フラグメントを表示させる
     */
    private fun showMakeGroupFragment(){
        showFragment(MakeGroupFragment.newInstance())
    }

    /**
     * 友達追加フラグメントを表示させる
     */
    private fun showAddDialogueFragment(){
        showFragment(AddDialogueFragment.newInstance())
    }

    /**
     * 通知有トークルームリストフラグメントを表示させる
     */
    private fun showTalkRoomListHasNoticeButtonFragment(){
        showFragment(TalkRoomListHasNoticeButtonFragment.newInstance())
    }

    private fun showFragment(fragment: Fragment){
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}