package towelman.server_on.net.chat_android

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

/**
 * トーク単体のView
 */
class TalkView(context: Context) : LinearLayout(context) {
    init {
        View.inflate(context, R.layout.talk_view, this)
    }

    /**
     * トークの中身をセットする
     *
     * @param contentText トークの内容
     */
    fun setContentText(contentText: String){
        val talkContentTextView = findViewById<TextView>(R.id.talkContentTextView)

        val textWidth = pixelsToDip(talkContentTextView.paint.measureText(contentText))
        talkContentTextView.setText(contentText, TextView.BufferType.NORMAL)
        if(getScreenWidth() * 0.7 < textWidth){
            talkContentTextView.layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7F)
        }
    }

    /**
     * 送信者を設定する<br>
     * 自分のトークの場合は設定しない
     *
     * @param userName 送信者のユーザー名
     */
    fun setSenderUserName(userName: String){
        val senderUserNameTextView = findViewById<TextView>(R.id.senderUserNameTextView)
        val thisContainer = findViewById<LinearLayout>(R.id.thisContainer)
        val talkContainer = findViewById<LinearLayout>(R.id.talkContainer)

        senderUserNameTextView.setText(userName, TextView.BufferType.NORMAL)
        senderUserNameTextView.visibility = VISIBLE

        thisContainer.setHorizontalGravity(Gravity.START)
        talkContainer.setHorizontalGravity(Gravity.START)
    }

    /**
     * 送信日時の文字列をセットする
     *
     * @param timestamp 送信日時の文字列
     */
    fun setTimestampTextView(timestamp: String){
        findViewById<TextView>(R.id.timestampTextView).setText(timestamp, TextView.BufferType.NORMAL)
    }

    /**
     * トーク単体自体をクリックしたときの処理をセットする
     *
     * @param clickListener クリックしたときの処理
     */
    fun setOnTalkClickListener(clickListener: OnClickListener) {
        findViewById<TextView>(R.id.talkContentTextView).setOnClickListener(clickListener)
    }

    /**
     * 現在の画面の幅を取得する
     *
     * @return 現在の画面の幅（dip）
     */
    private fun getScreenWidth(): Float{
        val displayMetrics = Resources.getSystem().displayMetrics
        return pixelsToDip(displayMetrics.widthPixels.toFloat())
    }

    /**
     * pxからdipに単位を変換
     *
     * @return dipの値
     */
    private fun pixelsToDip(pixels: Float) = pixels / Resources.getSystem().displayMetrics.density
}