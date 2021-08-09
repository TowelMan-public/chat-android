package towelman.server_on.net.chat_android

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import towelman.server_on.net.chat_android.model.*
import towelman.server_on.net.chat_android.updater.TalkRoomUpdater
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager

/**
 * 通知があるトークルームリストを表示するフラグメント<br>
 * A simple [Fragment] subclass.
 * Use the [TalkRoomListHasNoticeButtonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkRoomListHasNoticeButtonFragment : Fragment() {
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
        return inflater.inflate(
            R.layout.fragment_talk_room_list_has_notice_button,
            container,
            false
        )
    }

    /**
     * このFragmentのViewたちが生成されたときときの処理
     * このFragmentのViewたちの設定等
     *
     * @param view このFragmentのViewたち
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val talkRoomListContainer = view.findViewById<LinearLayout>(R.id.talkRoomListContainer)
        val talkRoomListUpdater = updateManager.getUpdater(UpdateKeyConfig.TALK_ROOM_LIST) as TalkRoomUpdater

        //TalkRoomListの更新後に実行すること
        talkRoomListUpdater.successDelegateList[javaClass.name] = {
            talkRoomListContainer.removeAllViews()

            //Vewたちのセット
            it!![DialogueTalkRoomModel::javaClass.name]!!.forEach { model ->
                if(model.noticeCount > 0)
                    addTalkRoomViewToContainer(talkRoomListContainer, model)
            }
            it[GroupTalkRoomModel::javaClass.name]!!.forEach { model ->
                if(model.noticeCount > 0)
                    addTalkRoomViewToContainer(talkRoomListContainer, model)
            }
            it[DesireDialogueTalkRoomModel::javaClass.name]!!.forEach { model ->
                addTalkRoomViewToContainer(talkRoomListContainer, model)
            }
            it[DesireGroupTalkRoomModel::javaClass.name]!!.forEach { model ->
                addTalkRoomViewToContainer(talkRoomListContainer, model)
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
         * @return A new instance of fragment TalkRoomListHasNoticeButtonFragment.
         */
        @JvmStatic
        fun newInstance() = TalkRoomListHasNoticeButtonFragment()
    }
}