package towelman.server_on.net.chat_android

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity.RESULT_OK
import android.icu.text.DateTimePatternGenerator
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import towelman.server_on.net.chat_android.client.exception.LoginException
import towelman.server_on.net.chat_android.handler.ExceptionHandler
import towelman.server_on.net.chat_android.service.UserRestService
import towelman.server_on.net.chat_android.validate.EditTextValidateManager
import towelman.server_on.net.chat_android.validate.EditTextValidator
import towelman.server_on.net.chat_android.validate.MaxStringValidatable
import towelman.server_on.net.chat_android.validate.NotBlankValidatable


/**
 * ログイン用のFragment<br>
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var thisView: View

    /**
     * このFragmentのActivityにアクセスしやすいようにするためのプロパティー
     */
    private val loginAndSignupActivity: LoginAndSignupActivity
        get() = (activity as LoginAndSignupActivity)

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

        //各必要なViewの取得
        val userIdNameTextEdit = view.findViewById<EditText>(R.id.userIdNameTextEdit)
        val passwordTextEdit = view.findViewById<EditText>(R.id.passwordTextEdit)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val signupTextView = view.findViewById<TextView>(R.id.signupTextView)

        //状態の復元
        if(savedInstanceState != null){
            userIdNameTextEdit.text = savedInstanceState.getCharSequenceArray("userIdNameTextEdit.text") as Editable
            passwordTextEdit.text = savedInstanceState.getCharSequenceArray("passwordTextEdit.text") as Editable
        }

        //ログインのValidateManager生成
        val loginValidateManager = EditTextValidateManager().apply {
            add(EditTextValidator(userIdNameTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
            add(EditTextValidator(passwordTextEdit).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
        }

        //ログインボタンのクリックイベント
        loginButton.setOnClickListener {
            //入力ﾁｪｯｸ
            if (loginValidateManager.doValidateList()) {
                loginAndSignupActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }

            login(userIdNameTextEdit.text.toString(), passwordTextEdit.text.toString())
        }

        //新規登録を提案する文字列のクリックイベント
        signupTextView.setOnClickListener {
            loginAndSignupActivity.showSignupFragment()
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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    /**
     * 状態の保存
     *
     * @param outState 状態を保存するクラス
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val userIdNameTextEdit = thisView.findViewById<EditText>(R.id.userIdNameTextEdit)
        val passwordTextEdit = thisView.findViewById<EditText>(R.id.passwordTextEdit)

        outState.putCharSequence("userIdNameTextEdit.text", userIdNameTextEdit.text)
        outState.putCharSequence("passwordTextEdit.text", passwordTextEdit.text)
    }

    /**
     * ログイン処理・この機種へのアカウント登録を行う関数
     *
     * @param userIdName ユーザーID名
     * @param password パスワード
     */
    private fun login(userIdName: String, password: String){
        //例外ハンドラーの作成
        val handlerList = loginAndSignupActivity.getCoroutineExceptionHandler()
        handlerList += ExceptionHandler.newIncense<LoginException>{
            AlertDialog.Builder(loginAndSignupActivity)
                .setTitle("失敗")
                .setMessage("ログインに失敗しました。ユーザーIDとパスワードをご確認ください。")
                .show()
            loginAndSignupActivity.stopShowingProgressBar()
        }

        //処理
        CoroutineScope(loginAndSignupActivity.coroutineContext).launch(handlerList.createCoroutineExceptionHandler()) {
            loginAndSignupActivity.startShowingProgressBar()
            withContext(Dispatchers.Default) {
                UserRestService.login(userIdName, password)
            }

            loginAndSignupActivity.accountManager.addAccount(userIdName, password)
            loginAndSignupActivity.transitionMainActivity()
            loginAndSignupActivity.stopShowingProgressBar()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}