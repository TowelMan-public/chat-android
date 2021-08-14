package towelman.server_on.net.chat_android

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import towelman.server_on.net.chat_android.client.exception.AlreadyHaveUserException
import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.service.DialogueRestService
import towelman.server_on.net.chat_android.service.GroupRestService
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager
import towelman.server_on.net.chat_android.validate.EditTextValidateManager
import towelman.server_on.net.chat_android.validate.EditTextValidator
import towelman.server_on.net.chat_android.validate.MaxStringValidatable
import towelman.server_on.net.chat_android.validate.NotBlankValidatable

/**
 * 友達登録用のFragment<br>
 * A simple [Fragment] subclass.
 * Use the [AddDialogueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddDialogueFragment : Fragment() {

    private lateinit var thisView: View

    /**
     * このFragmentのActivityにアクセスしやすいようにするためのプロパティー
     */
    private val mainActivity: MainActivity
        get() = (activity as MainActivity)

    /**
     * このFragmentが生成されたときの処理
     * このFragmentの設定等
     *
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val userIdNameTextEdit = view.findViewById<EditText>(R.id.userIdNameTextEdit)
        val addDialogueButton = view.findViewById<Button>(R.id.addDialogueButton)

        //状態の復元
        if(savedInstanceState != null){
            userIdNameTextEdit.text = savedInstanceState.getCharSequence("userIdNameTextEdit.text") as Editable
        }

        //友達追加のValidateManager生成
        val addDialogueValidateManager = EditTextValidateManager().apply {
            add(EditTextValidator(userIdNameTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
        }

        addDialogueButton.setOnClickListener {
            if(addDialogueValidateManager.doValidateList()) {
                mainActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }
            addDialogue(userIdNameTextEdit.text.toString())
        }
    }

    /**
     * このFragmentのUI等を生成するときの処理
     *
     * @param inflater 配置等を管理するやつ
     * @param container このFragmentを配置する場所
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_dialogue, container, false)
    }

    /**
     * 状態の保存
     *
     * @param outState 状態を保存するクラス
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val userIdNameTextEdit = thisView.findViewById<EditText>(R.id.userIdNameTextEdit)
        outState.putCharSequence("userIdNameTextEdit.text", userIdNameTextEdit.text)
    }

    /**
     * 友達追加をする
     *
     * @param userIdName 友達に追加したいユーザー
     */
    private fun addDialogue(userIdName: String){
        //エラーハンドラー作成
        val exceptionHandlingListForCoroutine = mainActivity.getExceptionHandlingListForCoroutine()
        exceptionHandlingListForCoroutine.add(
            ExceptionHandler.newIncense<AlreadyHaveUserException>{
                AlertDialog.Builder(mainActivity)
                    .setTitle("失敗")
                    .setMessage("既に友達追加されているユーザーが指定されました。もう一度ご確認ください。")
                    .show()
                mainActivity.stopShowingProgressBar()
            } + ExceptionHandler.newIncense<NotFoundException>{
                if(it.isErrorFieldUserIdName()) {
                    AlertDialog.Builder(mainActivity)
                        .setTitle("失敗")
                        .setMessage("あなたが指定したユーザーIDは存在しません。もう一度ご確認ください。")
                        .show()
                    mainActivity.stopShowingProgressBar()
                }else{
                    mainActivity.stopShowingProgressBar()
                    throw it
                }
            }
        )

        //処理

        CoroutineScope(mainActivity.coroutineContext).launch(exceptionHandlingListForCoroutine.createCoroutineExceptionHandler()) {
            mainActivity.startShowingProgressBar()
            withContext(Dispatchers.Default) {
                DialogueRestService.addDialogue(mainActivity.accountManager.getOauthToken(), userIdName)
            }

            Toast.makeText(context , "${userIdName}さんを友達登録しました。", Toast.LENGTH_LONG).show()
            UpdateManager.getInstance().getUpdater(UpdateKeyConfig.TALK_ROOM_LIST).runUpdate()
            mainActivity.stopShowingProgressBar()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AddDialogueFragment.
         */
        @JvmStatic
        fun newInstance() = AddDialogueFragment()
    }
}