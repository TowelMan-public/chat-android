package towelman.server_on.net.chat_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import towelman.server_on.net.chat_android.model.*
import towelman.server_on.net.chat_android.updater.TalkRoomUpdater
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager

/**
 * トークルームリストを表示するフラグメント<br>
 * A simple [Fragment] subclass.
 * Use the [TalkRoomListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkRoomListFragment : Fragment() {
    private val homeFragment: HomeFragment
        get() = parentFragment as HomeFragment
    private val updateManager = UpdateManager.getInstance()

    /**
     * このFragmentのUI等を生成するときの処理
     *
     * @param inflater 配置等を管理するやつ
     * @param container このFragmentを配置する場所
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_talk_room_list, container, false)
    }

    /**
     * このFragmentのViewたちが生成されたときときの処理
     * このFragmentのViewたちの設定等
     *
     * @param view このFragmentのViewたち
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //各種必要なもの取得
        val talkRoomListUpdater = updateManager.getUpdater(UpdateKeyConfig.TALK_ROOM_LIST) as TalkRoomUpdater
        val dialogueTalkRoomListContainerTitle = view.findViewById<ListTitleView>(R.id.dialogueTalkRoomListContainerTitle)
        val groupTalkRoomListContainerTitle = view.findViewById<ListTitleView>(R.id.groupTalkRoomListContainerTitle)
        val desireDialogueTalkRoomListContainerTitle = view.findViewById<ListTitleView>(R.id.desireDialogueTalkRoomListContainerTitle)
        val desireGroupTalkRoomListContainerTitle = view.findViewById<ListTitleView>(R.id.desireGroupTalkRoomListContainerTitle)
        val dialogueTalkRoomListContainer = view.findViewById<LinearLayout>(R.id.dialogueTalkRoomListContainer)
        val groupTalkRoomListContainer = view.findViewById<LinearLayout>(R.id.groupTalkRoomListContainer)
        val desireDialogueTalkRoomListContainer = view.findViewById<LinearLayout>(R.id.desireDialogueTalkRoomListContainer)
        val desireGroupTalkRoomListContainer = view.findViewById<LinearLayout>(R.id.desireGroupTalkRoomListContainer)

        //タイトルの部分の設定
        dialogueTalkRoomListContainerTitle.setTitleText("友達")
        dialogueTalkRoomListContainerTitle.setOnClickListener {
            switchContainerVisible(dialogueTalkRoomListContainer, dialogueTalkRoomListContainerTitle)
        }
        groupTalkRoomListContainerTitle.setTitleText("グループ")
        groupTalkRoomListContainerTitle.setOnClickListener {
            switchContainerVisible(groupTalkRoomListContainer, groupTalkRoomListContainerTitle)
        }
        desireDialogueTalkRoomListContainerTitle.setTitleText("友達追加申請者とのトークルーム")
        desireDialogueTalkRoomListContainerTitle.setOnClickListener {
            switchContainerVisible(desireDialogueTalkRoomListContainer, desireDialogueTalkRoomListContainerTitle)
        }
        desireGroupTalkRoomListContainerTitle.setTitleText("勧誘されているグループのトークルーム")
        desireGroupTalkRoomListContainerTitle.setOnClickListener {
            switchContainerVisible(desireGroupTalkRoomListContainer, desireGroupTalkRoomListContainerTitle)
        }

        //TalkRoomListの更新後に実行すること
        talkRoomListUpdater.successDelegateList[javaClass.name] = {
            //初期化
            dialogueTalkRoomListContainer.removeAllViews()
            groupTalkRoomListContainer.removeAllViews()
            desireDialogueTalkRoomListContainer.removeAllViews()
            desireGroupTalkRoomListContainer.removeAllViews()

            //Vewたちのセット
            it!![DialogueTalkRoomModel::javaClass.name]!!.forEach { model ->
                addTalkRoomViewToContainer(dialogueTalkRoomListContainer, model)
            }
            it[GroupTalkRoomModel::javaClass.name]!!.forEach { model ->
                addTalkRoomViewToContainer(groupTalkRoomListContainer, model)
            }
            it[DesireDialogueTalkRoomModel::javaClass.name]!!.forEach { model ->
                addTalkRoomViewToContainer(desireDialogueTalkRoomListContainer, model)
            }
            it[DesireGroupTalkRoomModel::javaClass.name]!!.forEach { model ->
                addTalkRoomViewToContainer(desireGroupTalkRoomListContainer, model)
            }
        }

        talkRoomListUpdater.runUpdate()
    }

    /**
     * このフラグメントが終わるときの処理
     */
    override fun onStop() {
        super.onStop()

        if(updateManager.isEnableUpdater(UpdateKeyConfig.TALK_ROOM_LIST)) {
            val talkRoomListUpdater = updateManager.getUpdater(UpdateKeyConfig.TALK_ROOM_LIST) as TalkRoomUpdater
            talkRoomListUpdater.successDelegateList.remove(javaClass.name)
        }
    }

    /**
     * container（LinearLayout）の表示、非表示をlistTitleViewの従って変更する
     */
    private fun switchContainerVisible(container: LinearLayout, listTitleView: ListTitleView){
        listTitleView.isOpened = !listTitleView.isOpened

        if(listTitleView.isOpened)
            container.visibility = View.VISIBLE
        else
            container.visibility = View.GONE
    }

    /**
     * containerにTalkRoomViewを追加する
     *
     * @param container 追加先のcontainer
     * @param talkRoomModel TalkRoomの内容
     */
    private fun addTalkRoomViewToContainer(container: LinearLayout, talkRoomModel: TalkRoomModel){
        //View作成
        val talkRoomView = TalkRoomView(activity!!).apply {
            setTalkRoomName(talkRoomModel.name)
            setTalkRoomNoticeCount(talkRoomModel.noticeCount)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            background = ResourcesCompat.getDrawable(resources, R.drawable.normal_border, null)

            setOnClickListener {
                homeFragment.showTalkListInTalkRoom(talkRoomModel)
            }
        }

        //追加
        container.addView(talkRoomView)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TalkRoomListFragment.
         */
        @JvmStatic
        fun newInstance() = TalkRoomListFragment()
    }
}