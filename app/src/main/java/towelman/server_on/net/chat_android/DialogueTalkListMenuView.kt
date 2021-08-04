package towelman.server_on.net.chat_android

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 友達トークリストのメニュー
 */
class DialogueTalkListMenuView(context: Context) : LinearLayout(context) {
    init{
        View.inflate(context, R.layout.dialogue_talk_list_menu_vuew, this)
    }

    /**
     * ブロックする文章がクリックしたときの処理のセット
     */
    fun setOnBrockTextViewTalkClickListener(clickListener: OnClickListener) {
        findViewById<TextView>(R.id.brockTextView).setOnClickListener(clickListener)
    }
}