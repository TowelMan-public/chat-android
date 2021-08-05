package towelman.server_on.net.chat_android

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import towelman.server_on.net.chat_android.validate.EditTextValidator
import towelman.server_on.net.chat_android.validate.MaxStringValidatable
import towelman.server_on.net.chat_android.validate.NotBlankValidatable

/**
 * トーク編集View
 */
class TalkEditView (context: Context) : LinearLayout(context) {
    init {
        View.inflate(context, R.layout.talk_edit_view, this)
    }

    /**
     * トークの内容のセット・取得
     */
    var contentText: String
        get() = findViewById<EditText>(R.id.contentEditText).text.toString()
        set(value){
            findViewById<EditText>(R.id.contentEditText).setText(value, TextView.BufferType.NORMAL)
        }

    /**
     * バリデーションチェックをする
     *
     * @return 引っかかったものあ有ればtrue、なければfasle
     */
    fun validate() = EditTextValidator(findViewById<EditText>(R.id.contentEditText)).apply {
        addValidatable(NotBlankValidatable())
        addValidatable(MaxStringValidatable(2000))
    }.runValidate() != null

    /**
     * 変更ボタンが押されたときの処理のセット
     */
    fun setOnChangeButtonClickListener(clickListener: OnClickListener) {
        findViewById<Button>(R.id.changeButton).setOnClickListener(clickListener)
    }

    /**
     * 削除ボタンが押されたときの処理のセット
     */
    fun setOnDeleteButtonClickListener(clickListener: OnClickListener) {
        findViewById<Button>(R.id.deleteButton).setOnClickListener(clickListener)
    }

    /**
     * 閉じるボタンが押されたときの処理のセット
     */
    fun setOnCloseButtonClickListener(clickListener: OnClickListener) {
        findViewById<Button>(R.id.closeButton).setOnClickListener(clickListener)
    }
}