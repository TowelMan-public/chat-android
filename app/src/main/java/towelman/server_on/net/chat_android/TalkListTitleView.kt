package towelman.server_on.net.chat_android

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * トークルームリストFragmentの一番上の部品
 */
class TalkListTitleView(context : Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    /**
     * このタイトルに対応するリストが表示されているかどうかのセット<br>
     * 表示されていればtrue、表示されていなければfalse
     */
    var isOpened: Boolean
        get() = findViewById<TextView>(R.id.isOpenedTextVew).text.toString() == "△"
        set(value){
            val isOpenedTextVew = findViewById<TextView>(R.id.isOpenedTextVew)
            if(value)
                isOpenedTextVew.setText("△", TextView.BufferType.NORMAL)
            else
                isOpenedTextVew.setText("▽", TextView.BufferType.NORMAL)
        }

    init {
        View.inflate(context, R.layout.talk_list_title_view, this)

        val isOpenedTextVew = findViewById<TextView>(R.id.isOpenedTextVew)
        isOpenedTextVew.setText("▽", TextView.BufferType.NORMAL)
    }

    /**
     * タイトルをセットする
     */
    fun setTitleText(title: String){
        val isOpenedTextVew = findViewById<TextView>(R.id.titleTextView)
        isOpenedTextVew.setText(title, TextView.BufferType.NORMAL)
    }
}