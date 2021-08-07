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
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import towelman.server_on.net.chat_android.client.exception.AlreadyUsedUserIdNameException
import towelman.server_on.net.chat_android.client.exception.NetworkOfflineException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.handler.ExceptionHandlingListForCoroutine
import towelman.server_on.net.chat_android.service.UserRestService
import towelman.server_on.net.chat_android.validate.*

/**
 * ユーザー設定のフラグメント
 * A simple [Fragment] subclass.
 * Use the [UserConfigFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserConfigFragment : Fragment() {
    private var isCreated: Boolean = false

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

        isCreated = savedInstanceState!!.getBoolean("isCreated")
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
        return inflater.inflate(R.layout.fragment_user_config, container, false)
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

        if(isCreated)
            restoreInstanceState(savedInstanceState!!)
        else
            setValueToAllEditTextView()

        setConfigToUserIdNameChangeButton()
        setConfigToUserNameChangeButton()
        setConfigToPasswordChangeButton()
        setConfigToLogoutTextView()
        setConfigWithdrawalTextView()
    }

    /**
     * 状態の保存
     *
     * @param outState 状態を保存するクラス
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val userIdNameEditText = view!!.findViewById<EditText>(R.id.userIdNameTextEdit)
        val userNameTextEdit = view!!.findViewById<EditText>(R.id.userNameTextEdit)
        val passwordTextEdit = view!!.findViewById<EditText>(R.id.passwordTextEdit)
        val oneMorePasswordTextEdit = view!!.findViewById<EditText>(R.id.oneMorePasswordTextEdit)

        outState.putBoolean("isCreated", true)
        outState.putCharSequence("userIdNameEditText.text", userIdNameEditText.text)
        outState.putCharSequence("userNameTextEdit.text", userNameTextEdit.text)
        outState.putCharSequence("passwordTextEdit.text", passwordTextEdit.text)
        outState.putCharSequence("oneMorePasswordTextEdit.text", oneMorePasswordTextEdit.text)
    }

    private fun setValueToAllEditTextView(){
        val userIdNameEditText = view!!.findViewById<EditText>(R.id.userIdNameTextEdit)
        val userNameTextEdit = view!!.findViewById<EditText>(R.id.userNameTextEdit)

        userIdNameEditText.setText(mainActivity.accountManager.userIdName)
        userNameTextEdit.setText(
            UserRestService.getUserName(mainActivity.accountManager.getOauthToken(), mainActivity.accountManager.userIdName),
            TextView.BufferType.NORMAL)
    }

    /**
     * 前回保存された状態に復元する
     *
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    private fun restoreInstanceState(savedInstanceState: Bundle) {
        val userIdNameEditText = view!!.findViewById<EditText>(R.id.userIdNameTextEdit)
        val userNameTextEdit = view!!.findViewById<EditText>(R.id.userNameTextEdit)
        val passwordTextEdit = view!!.findViewById<EditText>(R.id.passwordTextEdit)
        val oneMorePasswordTextEdit = view!!.findViewById<EditText>(R.id.oneMorePasswordTextEdit)

        userIdNameEditText.text = savedInstanceState.getCharSequence("userIdNameEditText.text") as Editable
        userNameTextEdit.text = savedInstanceState.getCharSequence("userNameTextEdit.text") as Editable
        passwordTextEdit.text = savedInstanceState.getCharSequence("passwordTextEdit.text") as Editable
        oneMorePasswordTextEdit.text = savedInstanceState.getCharSequence("oneMorePasswordTextEdit.text") as Editable
    }

    /**
     * ユーザーID名を変更するボタンの設定
     */
    private fun setConfigToUserIdNameChangeButton(){
        val userIdNameChangeButton = view!!.findViewById<Button>(R.id.userIdNameChangeButton)
        val userIdNameEditText = view!!.findViewById<EditText>(R.id.userIdNameTextEdit)

        //バリデーションチェックの設定
        val validateManager = EditTextValidateManager().apply {
            add(EditTextValidator(userIdNameEditText).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
        }

        //例外ハンドラーの設定
        val exceptionHandlerList =  ExceptionHandlingListForCoroutine().apply {
            add(
                mainActivity.getExceptionHandlingListForCoroutine() +
                    ExceptionHandler.newIncense<AlreadyUsedUserIdNameException> {
                        AlertDialog.Builder(mainActivity)
                                .setTitle("失敗")
                                .setMessage("あなたが指定したユーザーIDは既に使われています。ほかのものをご検討ください。")
                                .show()
                    }
            )
        }

        //処理の設定
        userIdNameChangeButton.setOnClickListener {
            if(validateManager.doValidateList()){
                mainActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }

            //処理
            CoroutineScope(mainActivity.coroutineContext).launch(exceptionHandlerList.createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    UserRestService.changeUserIdName(mainActivity.accountManager.getOauthToken(), userIdNameEditText.text.toString())
                }

                mainActivity.accountManager.userIdName = userIdNameEditText.text.toString()
            }
        }
    }

    /**
     * ユーザー名を変更するボタンの設定
     */
    private fun setConfigToUserNameChangeButton(){
        val userNameChangeButton = view!!.findViewById<Button>(R.id.userNameChangeButton)
        val userNameTextEdit = view!!.findViewById<EditText>(R.id.userNameTextEdit)

        //バリデーションチェックの設定
        val validateManager = EditTextValidateManager().apply {
            add(EditTextValidator(userNameTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
        }

        //処理の設定
        userNameChangeButton.setOnClickListener {
            if(validateManager.doValidateList()){
                mainActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }

            //処理
            CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    UserRestService.changeUserName(mainActivity.accountManager.getOauthToken(), userNameTextEdit.text.toString())
                }
            }
        }
    }

    /**
     * パスワードを変更するボタンの設定
     */
    private fun setConfigToPasswordChangeButton(){
        val passwordChangeButton = view!!.findViewById<Button>(R.id.passwordChangeButton)
        val passwordTextEdit = view!!.findViewById<EditText>(R.id.passwordTextEdit)
        val oneMorePasswordTextEdit = view!!.findViewById<EditText>(R.id.oneMorePasswordTextEdit)

        //バリデーションチェックの設定
        val validateManager = EditTextValidateManager().apply {
            add(EditTextValidator(passwordTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
            add(EditTextValidator(oneMorePasswordTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
                addValidatable(EqualEditTextValidatable(passwordTextEdit))
            })
        }

        //処理の設定
        passwordChangeButton.setOnClickListener {
            if(validateManager.doValidateList()){
                mainActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }

            //処理
            CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    UserRestService.changePassword(mainActivity.accountManager.getOauthToken(), passwordTextEdit.text.toString())
                }

                mainActivity.accountManager.password = passwordTextEdit.text.toString()
            }
        }
    }

    /**
     * ログアウトを促す文章(TextView)の設定
     */
    private fun setConfigToLogoutTextView(){
        val logoutTextView = view!!.findViewById<TextView>(R.id.logoutTextView)

        logoutTextView.setOnClickListener {
            homeFragment.finishForLogout()
        }
    }

    /**
     * 退会を促す文章(TextView)の設定
     */
    private fun setConfigWithdrawalTextView(){
        val withdrawalTextView = view!!.findViewById<TextView>(R.id.withdrawalTextView)

        withdrawalTextView.setOnClickListener {
            homeFragment.showWithdrawalFragment()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment UserConfigFragment.
         */
        @JvmStatic
        fun newInstance() =
            UserConfigFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isCreated", false)
                }
            }
    }
}