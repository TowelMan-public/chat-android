package towelman.server_on.net.chat_android

import android.icu.text.DateTimePatternGenerator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.*
import towelman.server_on.net.chat_android.client.exception.AlreadyUsedUserIdNameException
import towelman.server_on.net.chat_android.client.exception.LoginException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.service.UserRestService
import towelman.server_on.net.chat_android.validate.*


/**
 * ユーザーの新規登録用のFragment
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupFragment : Fragment() {
    /**
     * このFragmentのActivityにアクセスしやすいようにするためのプロパティー
     */
    private val loginAndSignupActivity: LoginAndSignupActivity
        get() = (activity as LoginAndSignupActivity)

    /**
     * このFragmentが生成されたときの処理
     *
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //各必要なViewの取得
        val userIdNameTextEdit = view!!.findViewById<EditText>(R.id.userIdNameTextEdit)
        val userNameTextEdit = view!!.findViewById<EditText>(R.id.userNameTextEdit)
        val passwordTextEdit = view!!.findViewById<EditText>(R.id.passwordTextEdit)
        val oneMorePasswordTextEdit = view!!.findViewById<EditText>(R.id.oneMorePassword)
        val signupButton = view!!.findViewById<Button>(R.id.signupButton)
        val loginTextView = view!!.findViewById<TextView>(R.id.loginTextView)

        //新規登録のValidateManager生成
        val signupValidateManager = EditTextValidateManager().apply {
            add(EditTextValidator(userIdNameTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
            add(EditTextValidator(userNameTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
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

        //新規登録ボタンのクリックイベント
        signupButton.setOnClickListener {
            //入力ﾁｪｯｸ
            if (signupValidateManager.doValidateList())
                loginAndSignupActivity.showValidationAlertDialogue()

            signup(userIdNameTextEdit.text.toString(), userNameTextEdit.text.toString(), passwordTextEdit.text.toString())
        }

        //ログインを提案する文字列のクリックイベント
        loginTextView.setOnClickListener {
            loginAndSignupActivity.showLoginFragment()
        }
    }

    /**
     * このFragmentのUI等を生成するときの処理
     *
     * @param inflater 配置等を管理するやつ
     * @param container このFragmentを配置する場所
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    /**
     * ユーザーの新規登録をする
     *
     * @param userIdName ユーザーID名
     * @param userName ユーザー名
     * @param password パスワード
     */
    private fun signup(userIdName: String, userName: String, password: String) {
        //例外ハンドラーの作成
        val handlerList = loginAndSignupActivity.getCoroutineExceptionHandler()
        handlerList += ExceptionHandler<AlreadyUsedUserIdNameException>{
            AlertDialog.Builder(loginAndSignupActivity)
                .setTitle("失敗")
                .setMessage("あなたが指定したユーザーIDは既に使われています。ほかのものをご検討ください。")
                .setPositiveButton(DateTimePatternGenerator.PatternInfo.OK, null)
                .show()
        }

        //処理
        CoroutineScope(loginAndSignupActivity.coroutineContext).launch(handlerList.createCoroutineExceptionHandler()) {
            withContext(Dispatchers.Default) {
                UserRestService.signup(userIdName, userName, password)
            }
            loginAndSignupActivity.showLoginFragment()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SignupFragment.
         */
        @JvmStatic
        fun newInstance() = SignupFragment()
    }
}