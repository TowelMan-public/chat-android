package towelman.server_on.net.chat_android

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import towelman.server_on.net.chat_android.service.GroupRestService
import towelman.server_on.net.chat_android.updater.UpdateKeyConfig
import towelman.server_on.net.chat_android.updater.UpdateManager
import towelman.server_on.net.chat_android.validate.EditTextValidateManager
import towelman.server_on.net.chat_android.validate.EditTextValidator
import towelman.server_on.net.chat_android.validate.MaxStringValidatable
import towelman.server_on.net.chat_android.validate.NotBlankValidatable


/**
 * グループ作成用のFragment<br>
 * A simple [Fragment] subclass.
 * Use the [MakeGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MakeGroupFragment : Fragment() {

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

        val groupNameEditText = view.findViewById<EditText>(R.id.groupNameTextEdit)
        val makeGroupButton = view.findViewById<Button>(R.id.makeGroupButton)

        //状態の復元
        if(savedInstanceState != null){
            groupNameEditText.text = savedInstanceState.getCharSequenceArray("groupNameEditText.text") as Editable
        }

        //グループ作成のValidateManager生成
        val groupValidateManager = EditTextValidateManager().apply {
            add(EditTextValidator(groupNameEditText).apply {
                addValidatable(NotBlankValidatable())
                addValidatable(MaxStringValidatable(100))
            })
        }

        //グループ作成ボタンが押されたときの処理
        makeGroupButton.setOnClickListener {
            if(groupValidateManager.doValidateList()) {
                mainActivity.showValidationAlertDialogue()
                return@setOnClickListener
            }
            makeGroup(groupNameEditText.text.toString())
        }
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
        return inflater.inflate(R.layout.fragment_make_group, container, false)
    }

    /**
     * 状態の保存
     *
     * @param outState 状態を保存するクラス
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val groupNameEditText = view!!.findViewById<EditText>(R.id.groupNameTextEdit)
        outState.putCharSequence("groupNameEditText.text", groupNameEditText.text)
    }

    /**
     * グループを作成する
     *
     * @param groupName グループ名
     */
    private fun makeGroup(groupName: String){
        //処理
        CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
            withContext(Dispatchers.Default) {
                GroupRestService.makeGroup(mainActivity.accountManager.getOauthToken(), groupName)
            }

            Toast.makeText(context , "グループを${groupName}という名前で作成しました", Toast.LENGTH_LONG).show()
            UpdateManager.getInstance().getUpdater(UpdateKeyConfig.TALK_ROOM_LIST).runUpdate()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MakeGroupFragment.
         */
        @JvmStatic
        fun newInstance() = MakeGroupFragment()
    }
}