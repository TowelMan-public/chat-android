package towelman.server_on.net.chat_android

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * リスト表示などに使うタイトルの部分
 */
class TitleView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.title_view, this)

        val isOpenedTextVew = findViewById<TextView>(R.id.isOpenedTextVew)
        isOpenedTextVew.setText("▽", TextView.BufferType.NORMAL)
    }

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



    /**
     * タイトルをセットする
     */
    fun setTitleText(title: String){
        val isOpenedTextVew = findViewById<TextView>(R.id.titleTextView)
        isOpenedTextVew.setText(title, TextView.BufferType.NORMAL)
    }
}