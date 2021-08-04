package towelman.server_on.net.chat_android

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * グループトークリストのメニュー
 */
class GroupTalkListMenuView(context: Context) : LinearLayout(context) {
    init{
        View.inflate(context, R.layout.dialogue_talk_list_menu_vuew, this)
    }

    /**
     * グループ詳細画面に案内する文章がクリックしたときの処理のセット
     */
    fun setOnShowGroupDetailsTextViewTalkClickListener(clickListener: OnClickListener) {
        findViewById<TextView>(R.id.showGroupDetailsTextView).setOnClickListener(clickListener)
    }
}