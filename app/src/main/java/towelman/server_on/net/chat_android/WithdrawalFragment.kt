package towelman.server_on.net.chat_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import towelman.server_on.net.chat_android.service.UserRestService

/**
 * 退会用のフラグメント
 * A simple [Fragment] subclass.
 * Use the [WithdrawalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WithdrawalFragment : Fragment() {
    private lateinit var thisView: View
    private val homeFragment: HomeFragment
        get() = parentFragment as HomeFragment
    private val mainActivity: MainActivity
        get() = (activity as MainActivity)

    /**
     * このFragmentが生成されたときの処理
     * このFragmentの設定等
     *
     * @param savedInstanceState このFragmentで保持するべき情報・状態
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_withdrawal, container, false)
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

        setConfigToWithdrawalButton()
        setConfigToBackButton()
    }

    /**
     * 退会するボタンの設定
     */
    private fun setConfigToWithdrawalButton(){
        val withdrawalButton = thisView.findViewById<Button>(R.id.withdrawalButton)

        withdrawalButton.setOnClickListener {
            mainActivity.startShowingProgressBar()
            CoroutineScope(mainActivity.coroutineContext).launch(mainActivity.getExceptionHandlingListForCoroutine().createCoroutineExceptionHandler()) {
                withContext(Dispatchers.Default) {
                    UserRestService.withdrawal(mainActivity.accountManager.getOauthToken())
                }
                mainActivity.finishForLogout()
            }

            mainActivity.stopShowingProgressBar()
        }
    }

    /**
     * 戻るボタンの設定
     */
    private fun setConfigToBackButton(){
        val backButton = thisView.findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {
            homeFragment.showUserConfigFragment()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment WithdrawalFragment.
         */
        @JvmStatic
        fun newInstance() = WithdrawalFragment()
    }
}