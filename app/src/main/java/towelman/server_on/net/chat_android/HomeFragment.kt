package towelman.server_on.net.chat_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import towelman.server_on.net.chat_android.model.TalkRoomModel

/**
 * ホーム画面の全体のFragment<br>
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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

    }

    /**
     * このFragmentのUI等を生成するときの処理
     *
     * @param inflater 配置等を管理するやつ
     * @param container このFragmentを配置する場所
     * @param savedInstanceState このActivityで保持するべき情報・状態
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * TODO
     * トークリストフラグメントを表示させる
     *
     * @param talkRoomModel 表示させたいトークルームのモデルクラス
     */
    fun showTalkListInTalkRoom(talkRoomModel: TalkRoomModel){
        //TODO
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    /*
          メモ

          ・通知有トークルームリストが存在するときの色: FF7700  ないときの色: 32C1ED
     */
}