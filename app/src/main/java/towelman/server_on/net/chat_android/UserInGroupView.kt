package towelman.server_on.net.chat_android

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * グループ加入者のView
 */
class UserInGroupView(context: Context): LinearLayout(context) {
    init {
        View.inflate(context, R.layout.user_in_group_view, this)
    }

    /**
     * 削除ボタンの表示状態をセットする
     *
     * @param visibility 表示状態
     */
    fun setDeleteButtonVisibility(visibility: Int){
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.visibility = visibility
    }

    /**
     * ユーザー名のセット
     *
     * @param userName ユーザー名
     */
    fun setUserName(userName: String){
        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        userNameTextView.setText(userName, TextView.BufferType.NORMAL)
    }

    /**
     * ユーザーID名のセット
     *
     * @param userIdName ユーザーID名
     */
    fun setUserIdName(userIdName: String){
        val userIdNameTextView = findViewById<TextView>(R.id.userIdNameTextView)
        userIdNameTextView.setText(userIdName, TextView.BufferType.NORMAL)
    }

    /**
     * 削除ボタンが押されたときの処理のセット
     *
     * @param onClickListener 削除ボタンが押されたときの処理
     */
    fun setOnDeleteButtonClickListener(onClickListener: OnClickListener){
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener(onClickListener)
    }

}