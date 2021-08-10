package towelman.server_on.net.chat_android

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.parceler.Parcels
import towelman.server_on.net.chat_android.client.exception.NotHaveUserException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine
import towelman.server_on.net.chat_android.model.*
import towelman.server_on.net.chat_android.service.DialogueRestService
import towelman.server_on.net.chat_android.service.GroupRestService
import towelman.server_on.net.chat_android.service.TalkRestService
import towelman.server_on.net.chat_android.validate.EditTextValidateManager
import towelman.server_on.net.chat_android.validate.EditTextValidator
import towelman.server_on.net.chat_android.validate.MaxStringValidatable
import towelman.server_on.net.chat_android.validate.NotBlankValidatable
import java.security.acl.Group
import javax.xml.validation.Validator

/**
 * トークリストのフラグメント
 * A simple [Fragment] subclass.
 * Use the [TalkListInTalkRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkListInTalkRoomFragment : Fragment() {
    private lateinit var talkRoomModel: TalkRoomModel
    private var newestIndex: Int = -1
    private var oldestIndex: Int = -1
    private var isCreated: Boolean = false

    private lateinit var errorHandlingListForCoroutine: ExceptionHandlingListForCoroutine
    private val homeFragment: HomeFragment
        get() = parentFragment as HomeFragment
    private val mainActivity: MainActivity
        get() = (activity as MainActivity)

    /**
     * このFragmentが生成されたときの処理
     *
     * @param savedInstanceState このFragmentで保持するべき情報・状態
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        talkRoomModel = getTalkRoomModelByBundle(savedInstanceState!!)
        isCreated = savedInstanceState.getBoolean("isCreated")
    }

    /**
     * BundleからModelクラスを取得する
     *
     * @param savedInstanceState 保存されているデータたち
     */
    private fun getTalkRoomModelByBundle(savedInstanceState: Bundle): TalkRoomModel{
        var parcelable = savedInstanceState.getParcelable<Parcelable>("DialogueTalkRoomModel")
        if(parcelable != null)
            return Parcels.unwrap<DialogueTalkRoomModel>(parcelable)

        parcelable = savedInstanceState.getParcelable<Parcelable>("GroupTalkRoomModel")
        if(parcelable != null)
            return Parcels.unwrap<GroupTalkRoomModel>(parcelable)

        parcelable = savedInstanceState.getParcelable<Parcelable>("DesireDialogueTalkRoomModel")
        if(parcelable != null)
            return Parcels.unwrap<DesireDialogueTalkRoomModel>(parcelable)

        parcelable = savedInstanceState.getParcelable<Parcelable>("DesireGroupTalkRoomModel")
        return Parcels.unwrap<DesireGroupTalkRoomModel>(parcelable)
    }

    /**
     * このFragmentのUI等を生成するときの処理
     *
     * @param inflater 配置等を管理するやつ
     * @param container このFragmentを配置する場所
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_talk_list_in_talk_room, container, false)
    }

    /**
     * 状態の保存
     *
     * @param outState 状態を保存するクラス
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val contentEditText = view!!.findViewById<EditText>(R.id.contentEditText)

        outState.putString("contentEditText.text", contentEditText.text.toString())
        outState.putBoolean("isCreated", true)
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

        if(isCreated){
            val contentEditText = view.findViewById<EditText>(R.id.contentEditText)
            contentEditText.setText(savedInstanceState!!.getString("contentEditText.text"))

            view.findViewById<LinearLayout>(R.id.menuContainer).removeAllViews()
            view.findViewById<ListTitleView>(R.id.talkListContainer).removeAllViews()
        }

        errorHandlingListForCoroutine = mainActivity.getExceptionHandlingListForCoroutine()
        setConfigToTitleTextView()
        setMenuViewToMenuContainer()
        loadTalkList()
        setConfigToOlderLoadButton()
        setConfigToNewerLoadButton()
        setConfigToSendButton()
    }

    /**
     * タイトルの部分の設定をする
     */
    private fun setConfigToTitleTextView(){
        val titleTextView = view!!.findViewById<TalkListTitleView>(R.id.titleTextView)
        val menuContainer = view!!.findViewById<LinearLayout>(R.id.menuContainer)

        titleTextView.setTitleText(talkRoomModel.name)
        titleTextView.setOnClickListener {
            titleTextView.isOpened = !titleTextView.isOpened

            menuContainer.visibility = if(titleTextView.isOpened)
                                            View.VISIBLE
                                       else
                                            View.GONE
        }
    }

    /**
     * メニューViewの設定をする
     */
    private fun setMenuViewToMenuContainer(){
        val menuContainer = view!!.findViewById<LinearLayout>(R.id.menuContainer)

        val menuView: View =
            when (talkRoomModel) {
                is DialogueTalkRoomModel -> DialogueTalkListMenuView(mainActivity).apply {
                    setOnBrockTextViewTalkClickListener(View.OnClickListener {
                        mainActivity.startShowingProgressBar()
                        CoroutineScope(mainActivity.coroutineContext).launch(errorHandlingListForCoroutine.createCoroutineExceptionHandler()) {
                            withContext(Dispatchers.Default) {
                                DialogueRestService.brock(
                                    mainActivity.accountManager.getOauthToken(),
                                    (talkRoomModel as DialogueTalkRoomModel).haveUserIdName
                                )
                            }
                        }
                        homeFragment.closeChildFragment()
                        mainActivity.stopShowingProgressBar()
                    })
                }

                is GroupTalkRoomModel -> GroupTalkListMenuView(mainActivity).apply {
                    setOnShowGroupDetailsTextViewTalkClickListener(View.OnClickListener {
                        homeFragment.showGroupDetailsFragment(talkRoomModel as GroupTalkRoomModel)
                    })
                }

                else -> DesireTalkListMenuView(mainActivity).apply {
                    setOnAcceptTextViewTalkClickListener(View.OnClickListener {
                        mainActivity.startShowingProgressBar()
                        CoroutineScope(mainActivity.coroutineContext).launch(errorHandlingListForCoroutine.createCoroutineExceptionHandler()) {
                            withContext(Dispatchers.Default) {
                                if (talkRoomModel is DesireDialogueTalkRoomModel) {
                                    DialogueRestService.acceptDesire(
                                        mainActivity.accountManager.getOauthToken(),
                                        (talkRoomModel as DesireDialogueTalkRoomModel).haveUserIdName
                                    )
                                } else {
                                    GroupRestService.brockDesire(
                                        mainActivity.accountManager.getOauthToken(),
                                        (talkRoomModel as DesireGroupTalkRoomModel).groupTalkRoomId
                                    )
                                }
                            }
                        }
                        mainActivity.stopShowingProgressBar()
                        homeFragment.closeChildFragment()
                    })

                    setOnBrockTextViewTalkClickListener(View.OnClickListener {
                        mainActivity.startShowingProgressBar()
                        CoroutineScope(mainActivity.coroutineContext).launch(errorHandlingListForCoroutine.createCoroutineExceptionHandler()) {
                            withContext(Dispatchers.Default) {
                                if (talkRoomModel is DesireDialogueTalkRoomModel) {
                                    DialogueRestService.brockDesire(
                                        mainActivity.accountManager.getOauthToken(),
                                        (talkRoomModel as DesireDialogueTalkRoomModel).haveUserIdName
                                    )
                                } else {
                                    GroupRestService.acceptDesire(
                                        mainActivity.accountManager.getOauthToken(),
                                        (talkRoomModel as DesireGroupTalkRoomModel).groupTalkRoomId
                                    )
                                }
                            }
                        }
                        mainActivity.stopShowingProgressBar()
                        homeFragment.closeChildFragment()
                    })
                }
            }

        menuView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        menuContainer.addView(menuView)
    }

    /**
     * トークを読み込み表示する<br>
     * 初回盤
     */
    private fun loadTalkList(){
        loadTalkList(talkRoomModel.lastTalkIndex - 25, 50)
    }

    /**
     * トークを読み込み表示する
     *
     * @param startIndex 取得開始インデックス
     * @param maxSize 最大取得件数
     * @param isOlder 古いトークを追加で読み込むならtrue、そうでないのならfalseを指定する。デフォルトはfalse
     */
    private fun loadTalkList(startIndex: Int, maxSize: Int, isOlder: Boolean = false){
        var talkModelList: MutableList<TalkModel> = mutableListOf()

        mainActivity.startShowingProgressBar()
        CoroutineScope(mainActivity.coroutineContext).launch(errorHandlingListForCoroutine.createCoroutineExceptionHandler()) {
            withContext(Dispatchers.Default) {
                talkModelList = when (talkRoomModel) {
                    is DialogueTalkRoomModel -> {
                        TalkRestService.getDialogueTalkList(mainActivity.accountManager.getOauthToken(),
                            mainActivity.accountManager.userIdName, (talkRoomModel as DialogueTalkRoomModel).haveUserIdName, startIndex, maxSize)
                    }
                    is GroupTalkRoomModel -> {
                        TalkRestService.getGroupTalkList(mainActivity.accountManager.getOauthToken(),
                            mainActivity.accountManager.userIdName, (talkRoomModel as DesireGroupTalkRoomModel).groupTalkRoomId, startIndex, maxSize)
                    }
                    is DesireDialogueTalkRoomModel -> {
                        TalkRestService.getGroupTalkList(mainActivity.accountManager.getOauthToken(),
                            mainActivity.accountManager.userIdName, (talkRoomModel as DesireGroupTalkRoomModel).groupTalkRoomId, startIndex, maxSize)
                    }
                    else -> {
                        TalkRestService.getDialogueTalkList(mainActivity.accountManager.getOauthToken(),
                            mainActivity.accountManager.userIdName, (talkRoomModel as DesireDialogueTalkRoomModel).haveUserIdName, startIndex, maxSize)
                    }
                }
            }
        }

        if(isOlder)
            talkModelList.reverse()

        talkModelList.forEach {
            //インデックスの保存
            if(oldestIndex == -1 || oldestIndex > it.talkIndex)
                oldestIndex = it.talkIndex
            if(newestIndex == -1 || newestIndex < it.talkIndex)
                newestIndex = it.talkIndex

            //表示
            showTalkView(it, isOlder)
        }

        mainActivity.stopShowingProgressBar()
    }

    /**
     * トーク単体に対するViewを表示する
     *
     * @param talkModel 対象のトークのモデルクラス
     * @param isOlder 古いトークを追加で読み込むならtrue、そうでないのならfalseを指定する。デフォルトはfalse
     */
    private fun showTalkView(talkModel: TalkModel, isOlder: Boolean = false) {
        val talkListContainer = view!!.findViewById<ListTitleView>(R.id.talkListContainer)

        val talkView = TalkView(mainActivity).apply {
            setContentText(talkModel.contentText)
            setTimestampTextView(talkModel.timestamp)

            if(talkModel.isMyTalk){
                setOnTalkClickListener(View.OnClickListener {
                    showTalkEditView(talkModel, this)
                })
            }else{
                setSenderUserName(talkModel.SenderUserName)
            }
        }

        if(isOlder)
            talkListContainer.addView(talkView, 0)
        else
            talkListContainer.addView(talkView)
    }

    /**
     * トーク編集Viewを表示する
     *
     * @param talkModel 対象のトークのモデルクラス
     * @param talkView 対象のトークのView
     */
    private fun showTalkEditView(talkModel: TalkModel, talkView: TalkView){
        val titleTextView = view!!.findViewById<TalkListTitleView>(R.id.titleTextView)
        val editTextContainer = view!!.findViewById<LinearLayout>(R.id.editTextContainer)
        val bodyContainer = view!!.findViewById<LinearLayout>(R.id.bodyContainer)

        val talkEditView = TalkEditView(mainActivity).apply {
            contentText = talkModel.contentText

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            setOnCloseButtonClickListener(View.OnClickListener {
                closeTalkEditView()
            })

            setOnChangeButtonClickListener(View.OnClickListener {
                if(validate()){
                    mainActivity.showValidationAlertDialogue()
                    return@OnClickListener
                }

                changeTalk(talkView, talkModel.talkIndex, contentText)
            })

            setOnDeleteButtonClickListener(View.OnClickListener {
                deleteTalk(talkView, talkModel.talkIndex)
            })
        }

        editTextContainer.visibility = View.VISIBLE
        editTextContainer.addView(talkEditView)
        bodyContainer.visibility = View.GONE
        titleTextView.visibility = View.GONE
    }

    /**
     * トークの内容を変更する
     *
     * @param talkView 対象のトークのView
     * @param talkIndex トークインデックス
     * @param newContentText 新しいトークの内容（変更後のこと）
     */
    private fun changeTalk(talkView: TalkView, talkIndex: Int, newContentText: String){
        mainActivity.startShowingProgressBar()
        CoroutineScope(mainActivity.coroutineContext).launch(errorHandlingListForCoroutine.createCoroutineExceptionHandler()) {
            withContext(Dispatchers.Default) {
                if (talkRoomModel is DialogueTalkRoomModel) {
                    TalkRestService.changeDialogueTalk(mainActivity.accountManager.getOauthToken(),
                        (talkRoomModel as DialogueTalkRoomModel).haveUserIdName, talkIndex, newContentText)
                } else if (talkRoomModel is GroupTalkRoomModel) {
                    TalkRestService.changeGroupTalk(mainActivity.accountManager.getOauthToken(),
                        (talkRoomModel as GroupTalkRoomModel).groupTalkRoomId, talkIndex, newContentText)
                }
            }
            talkView.setContentText(newContentText)
        }
        mainActivity.stopShowingProgressBar()
    }

    /**
     * トークを削除する
     *
     * @param talkView 対象のトークのView
     * @param talkIndex トークインデックス
     */
    private fun deleteTalk(talkView: TalkView, talkIndex: Int){
        val talkListContainer = view!!.findViewById<ListTitleView>(R.id.talkListContainer)

        mainActivity.startShowingProgressBar()
        CoroutineScope(mainActivity.coroutineContext).launch(errorHandlingListForCoroutine.createCoroutineExceptionHandler()) {
            withContext(Dispatchers.Default) {
                if (talkRoomModel is DialogueTalkRoomModel) {
                    TalkRestService.deleteDialogueTalk(mainActivity.accountManager.getOauthToken(),
                        (talkRoomModel as DialogueTalkRoomModel).haveUserIdName, talkIndex)
                } else if (talkRoomModel is GroupTalkRoomModel) {
                    TalkRestService.deleteGroupTalk(mainActivity.accountManager.getOauthToken(),
                        (talkRoomModel as GroupTalkRoomModel).groupTalkRoomId, talkIndex)
                }
            }

            talkListContainer.removeView(talkView)
            closeTalkEditView()
        }
        mainActivity.stopShowingProgressBar()
    }

    /**
     * トーク編集Viewを閉じる
     */
    private fun closeTalkEditView(){
        val titleTextView = view!!.findViewById<TalkListTitleView>(R.id.titleTextView)
        val editTextContainer = view!!.findViewById<LinearLayout>(R.id.editTextContainer)
        val bodyContainer = view!!.findViewById<LinearLayout>(R.id.bodyContainer)

        editTextContainer.removeAllViews()
        editTextContainer.visibility = View.GONE
        bodyContainer.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    /**
     * もっと古いトークを読み込むボタンの設定をする
     */
    private fun setConfigToOlderLoadButton(){
        val olderLoadButton = view!!.findViewById<Button>(R.id.olderLoadButton)

        olderLoadButton.setOnClickListener {
            if(oldestIndex == -1)
                loadTalkList()
            else
                loadTalkList(oldestIndex - 26, 25, true)
        }
    }

    /**
     * もっと新しいトークを読み込むボタンの設定をする
     */
    private fun setConfigToNewerLoadButton(){
        val newerLoadButton = view!!.findViewById<Button>(R.id.newerLoadButton)

        newerLoadButton.setOnClickListener {
            if(newestIndex == -1)
                loadTalkList()
            else
                loadTalkList(newestIndex + 1, 25)
        }
    }

    /**
     * 送信の設定をする
     */
    private fun setConfigToSendButton(){
        val contentEditText = view!!.findViewById<EditText>(R.id.contentEditText)
        val sendButton = view!!.findViewById<Button>(R.id.sendButton)

        val sendBoxValidateManager = EditTextValidateManager().apply {
            add(EditTextValidator(contentEditText).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(2000))
            })
        }

        sendButton.setOnClickListener {
            mainActivity.startShowingProgressBar()
            CoroutineScope(mainActivity.coroutineContext).launch(errorHandlingListForCoroutine.createCoroutineExceptionHandler()) {
                if (!sendBoxValidateManager.doValidateList()) {
                    withContext(Dispatchers.Default) {
                        if (talkRoomModel is DialogueTalkRoomModel) {
                            TalkRestService.sendDialogueTalk(
                                mainActivity.accountManager.getOauthToken(),
                                (talkRoomModel as DialogueTalkRoomModel).haveUserIdName,
                                contentEditText.text.toString()
                            )
                        } else if (talkRoomModel is GroupTalkRoomModel) {
                            TalkRestService.sendGroupTalk(
                                mainActivity.accountManager.getOauthToken(),
                                (talkRoomModel as GroupTalkRoomModel).groupTalkRoomId,
                                contentEditText.text.toString()
                            )
                        }
                    }

                    contentEditText.setText("", TextView.BufferType.NORMAL)
                }
            }
            mainActivity.stopShowingProgressBar()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param talkRoomModel 表示させたいトークルームモデル
         * @return A new instance of fragment TalkListInTalkRoomFragment.
         */
        @JvmStatic
        fun newInstance(talkRoomModel: TalkRoomModel) =
            TalkListInTalkRoomFragment().apply {
                arguments = Bundle().apply {
                    //talkRoomModel
                    if(talkRoomModel is DialogueTalkRoomModel)
                        putParcelable("DialogueTalkRoomModel", Parcels.wrap(talkRoomModel))
                    if(talkRoomModel is GroupTalkRoomModel)
                        putParcelable("GroupTalkRoomModel", Parcels.wrap(talkRoomModel))
                    if(talkRoomModel is DesireDialogueTalkRoomModel)
                        putParcelable("DesireDialogueTalkRoomModel", Parcels.wrap(talkRoomModel))
                    if(talkRoomModel is DesireGroupTalkRoomModel)
                        putParcelable("DesireGroupTalkRoomModel", Parcels.wrap(talkRoomModel))

                    putBoolean("isCreated", false)
                }
            }
    }
}