package towelman.server_on.net.chat_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import towelman.server_on.net.chat_android.client.entity.UserInGroupResponse
import towelman.server_on.net.chat_android.client.exception.AlreadyInsertedGroupDesireException
import towelman.server_on.net.chat_android.client.exception.AlreadyInsertedGroupException
import towelman.server_on.net.chat_android.client.exception.AlreadyUsedUserIdNameException
import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine
import towelman.server_on.net.chat_android.service.GroupRestService
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager
import towelman.server_on.net.chat_android.updater.Updater
import towelman.server_on.net.chat_android.validate.EditTextValidateManager
import towelman.server_on.net.chat_android.validate.EditTextValidator
import towelman.server_on.net.chat_android.validate.MaxStringValidatable
import towelman.server_on.net.chat_android.validate.NotBlankValidatable

/**
 * グループ詳細フラグメント
 * A simple [Fragment] subclass.
 * Use the [GroupDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupDetailsFragment : Fragment() {
    private lateinit var thisView: View
    private var isCreated: Boolean = false
    private var groupTalkRoomId: Int = -1

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
        arguments?.let {
            groupTalkRoomId = it.getInt("groupTalkRoomId")
            isCreated = it.getBoolean("isCreated")
        }
    }

    /**
     * このFragmentのUI等を生成するときの処理
     *
     * @param inflater 配置等を管理するやつ
     * @param container このFragmentを配置する場所
     * @param savedInstanceState このFragmentで保持するべき情報・状態
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_details, container, false)
    }

    /**
     * このFragmentのViewたちが生成されたときときの処理
     * このFragmentのViewたちの設定等
     *
     * @param view このFragmentのViewたち
     * @param savedInstanceState このFragmentで保持するべき情報・状態
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view

        if (isCreated)
            restoreInstanceState(savedInstanceState!!)
        else
            setValueToAllView()

        setConfigToUpdater()
        setConfigToChangeGroupNameButton()
        setConfigToDeleteGroupTextView()
        setConfigToExitGroupTextView()
        setConfigToInvitationGroupButton()
    }

    /**
     * 状態の保存
     *
     * @param outState 状態を保存するクラス
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val groupNameTextEdit = thisView.findViewById<TextView>(R.id.groupNameTextEdit)
        val userIdNameForInvitationTextEdit = thisView.findViewById<TextView>(R.id.userIdNameForInvitationTextEdit)

        outState.putCharSequence("groupNameTextEdit.text", groupNameTextEdit.text)
        outState.putCharSequence("userIdNameForInvitationTextEdit.text", userIdNameForInvitationTextEdit.text)
    }

    /**
     * このフラグメントが壊されるときの処理
     */
    override fun onDestroy() {
        super.onDestroy()

        UpdateManager.getInstance().deleteUpdater(UpdateKeyConfig.USER_IN_GROUP_LIST)
    }

    /**
     * Viewたちの状態の復元
     *
     * @param savedInstanceState このFragmentで保持するべき情報・状態
     */
    private fun restoreInstanceState(savedInstanceState: Bundle){
        val groupNameTextEdit = thisView.findViewById<TextView>(R.id.groupNameTextEdit)
        val userIdNameForInvitationTextEdit = thisView.findViewById<TextView>(R.id.userIdNameForInvitationTextEdit)

        groupNameTextEdit.text = savedInstanceState.getCharSequence("groupNameTextEdit.text")
        userIdNameForInvitationTextEdit.text = savedInstanceState.getCharSequence("userIdNameForInvitationTextEdit.text")
    }

    /**
     * 値をセットするべきViewたちに値をセットする
     */
    private fun setValueToAllView(){
        val groupNameTextEdit = thisView.findViewById<TextView>(R.id.groupNameTextEdit)

        mainActivity.startShowingProgressBar()
        CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
            val groupName = withContext(Dispatchers.Default) {
                GroupRestService.getGroupName(mainActivity.accountManager.getOauthToken(), groupTalkRoomId)
            }
            groupNameTextEdit.setText(groupName, TextView.BufferType.NORMAL)
        }
        mainActivity.stopShowingProgressBar()
    }

    /**
     * このフラグメントで使うUpdaterを設定する
     */
    private fun setConfigToUpdater(){
        val userInGroupViewUpdater = Updater<List<UserInGroupResponse>>()

        //更新処理
        userInGroupViewUpdater.updateDelegate = {
            GroupRestService.getUserInGroupList(mainActivity.accountManager.getOauthToken(), groupTalkRoomId)
        }

        //例外ハンドラー
        userInGroupViewUpdater.exceptionHandlingList = mainActivity.getExceptionHandlingListForCoroutine()

        //成功時の処理
        userInGroupViewUpdater.successDelegateList[this::class.java.name] = {
            val userInGroupContainer = thisView.findViewById<LinearLayout>(R.id.userInGroupContainer)
            userInGroupContainer.removeAllViews()

            //Viewたちを表示・設定
            it!!.forEach { response ->
                val userInGroupView = UserInGroupView(mainActivity).apply {
                    setUserIdName(response.userIdName)
                    setUserName(response.userName)
                    setOnDeleteButtonClickListener(View.OnClickListener{
                        deleteUserInGroup(this, response.userIdName)
                    })
                }

                userInGroupContainer.addView(userInGroupView)
            }
        }

        //登録
        UpdateManager.getInstance().addUpdater(UpdateKeyConfig.USER_IN_GROUP_LIST, userInGroupViewUpdater)
        UpdateManager.getInstance().setUpdateTimeSpan(UpdateKeyConfig.USER_IN_GROUP_LIST, 5 * 60000)
    }

    /**
     * 加入者を脱退させる
     *
     * @param userInGroupView 脱退させる加入者を表示しているView
     * @param userIdName 脱退させるユーザーのID名
     */
    private fun deleteUserInGroup(userInGroupView: UserInGroupView, userIdName: String){
        //実行しないやつを省く
        if(userIdName == mainActivity.accountManager.userIdName)
            return

        val userInGroupContainer = thisView.findViewById<LinearLayout>(R.id.userInGroupContainer)

        mainActivity.startShowingProgressBar()
        CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
            withContext(Dispatchers.Default) {
                GroupRestService.deleteUserInGroup(mainActivity.accountManager.getOauthToken(), groupTalkRoomId, userIdName)
            }
        }

        userInGroupContainer.removeView(userInGroupView)
        mainActivity.stopShowingProgressBar()
    }

    /**
     * グループ名を変更するボタンの設定
     */
    private fun setConfigToChangeGroupNameButton(){
        val groupNameChangeButton = thisView.findViewById<Button>(R.id.groupNameChangeButton)
        val groupNameTextEdit = thisView.findViewById<EditText>(R.id.groupNameTextEdit)

        //バリデーションチェックの設定
        val validateManager = EditTextValidateManager().apply {
            add(EditTextValidator(groupNameTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
        }

        //クリック時の処理の設定
        groupNameChangeButton.setOnClickListener {
            if(validateManager.doValidateList()){
                mainActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }

            mainActivity.startShowingProgressBar()
            CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    GroupRestService.changeGroupName(mainActivity.accountManager.getOauthToken(), groupTalkRoomId, groupNameTextEdit.text.toString())
                }
            }
            mainActivity.stopShowingProgressBar()
        }
    }

    /**
     * グループを削除することを促す文章がクリックされたときの設定
     */
    private fun setConfigToDeleteGroupTextView(){
        val deleteGroupTextView = thisView.findViewById<TextView>(R.id.deleteGroupTextView)

        deleteGroupTextView.setOnClickListener {
            mainActivity.startShowingProgressBar()
            CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    GroupRestService.deleteGroup(mainActivity.accountManager.getOauthToken(), groupTalkRoomId)
                }
            }

            homeFragment.closeChildFragment()
            mainActivity.stopShowingProgressBar()
        }
    }

    /**
     * グループから脱退することを促す文章をクリックしたときのイベント
     */
    private fun setConfigToExitGroupTextView(){
        val exitGroupTextView = thisView.findViewById<TextView>(R.id.exitGroupTextView)

        exitGroupTextView.setOnClickListener {
            mainActivity.startShowingProgressBar()
            CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    GroupRestService.exitGroup(mainActivity.accountManager.getOauthToken(), groupTalkRoomId)
                }
            }

            homeFragment.closeChildFragment()
            mainActivity.stopShowingProgressBar()
        }
    }

    /**
     * グループに勧誘するボタンの設定
     */
    private fun setConfigToInvitationGroupButton(){
        val invitationToGroupButton = thisView.findViewById<Button>(R.id.invitationToGroupButton)
        val userIdNameForInvitationTextEdit = thisView.findViewById<EditText>(R.id.userIdNameForInvitationTextEdit)

        //バリデーションチェックの設定
        val validateManager = EditTextValidateManager().apply {
            add(EditTextValidator(userIdNameForInvitationTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
        }

        //例外ハンドラーの設定
        val exceptionHandlerList =  ExceptionHandlingListForCoroutine().apply {
            add(
                mainActivity.getExceptionHandlingListForCoroutine() +
                    ExceptionHandler.newIncense<AlreadyInsertedGroupException> {
                        AlertDialog.Builder(mainActivity)
                                .setTitle("失敗")
                                .setMessage("あなたが指定したユーザーIDは既に加入しています。")
                                .show()
                    } +
                    ExceptionHandler.newIncense<AlreadyInsertedGroupDesireException> {
                        AlertDialog.Builder(mainActivity)
                                .setTitle("失敗")
                                .setMessage("あなたが指定したユーザーは既に勧誘中です。勧誘を受けるまでお待ちください。")
                                .show()
                    } +
                    ExceptionHandler.newIncense<NotFoundException> {
                        if(!it.isErrorFieldUserIdName())
                            throw it

                        AlertDialog.Builder(mainActivity)
                                .setTitle("失敗")
                                .setMessage("あなたが指定したユーザーIDは存在しません。もう一度ご確認ください。")
                                .show()
                    }
            )
        }

        //クリック時の処理の設定
        invitationToGroupButton.setOnClickListener {
            if(validateManager.doValidateList()){
                mainActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }

            mainActivity.startShowingProgressBar()
            CoroutineScope(mainActivity.coroutineContext).launch(exceptionHandlerList.createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    GroupRestService.invitationUserToGroup(mainActivity.accountManager.getOauthToken(),
                            groupTalkRoomId, userIdNameForInvitationTextEdit.text.toString())
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
         * @param groupTalkRoomId グループトークルームID
         * @return A new instance of fragment GroupDetailsFragment.
         */
        @JvmStatic
        fun newInstance(groupTalkRoomId: Int) =
                GroupDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putInt("groupTalkRoomId", groupTalkRoomId)
                        putBoolean("isCreated", false)
                    }
                }
    }
}