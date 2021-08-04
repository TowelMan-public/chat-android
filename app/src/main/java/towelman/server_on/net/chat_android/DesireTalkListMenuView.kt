package towelman.server_on.net.chat_android

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 友達追加申請者とのトークリストと勧誘されているグループのトークリストのメニュー
 */
class DesireTalkListMenuView(context: Context) : LinearLayout(context) {
    init{
        View.inflate(context, R.layout.desire_talk_list_menu_view, this)
    }

    /**
     * ブロックする文章がクリックしたときの処理のセット
     */
    fun setOnBrockTextViewTalkClickListener(clickListener: OnClickListener) {
        findViewById<TextView>(R.id.brockTextView).setOnClickListener(clickListener)
    }

    /**
     * 「受け入れる」という文章がクリックしたときの処理のセット
     */
    fun setOnAcceptTextViewTalkClickListener(clickListener: OnClickListener) {
        findViewById<TextView>(R.id.acceptTextView).setOnClickListener(clickListener)
    }
}