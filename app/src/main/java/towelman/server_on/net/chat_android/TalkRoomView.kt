package towelman.server_on.net.chat_android

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * トークルームのView
 */
class TalkRoomView : LinearLayout {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    init {
        View.inflate(context, R.layout.talk_room_view, this)
    }

    /**
     * トークルーム名をセットする
     *
     * @param talkRoomName トークルーム名
     */
    fun setTalkRoomName(talkRoomName: String){
        findViewById<TextView>(R.id.talkRoomNameTextView).setText(talkRoomName, TextView.BufferType.NORMAL)
    }

    /**
     * トークルームの通知数をセットする
     *
     * @param talkRoomNoticeCount トークルームの通知数
     */
    fun setTalkRoomNoticeCount(talkRoomNoticeCount: Int){
        val talkRoomNoticeTextView = findViewById<TextView>(R.id.talkRoomNoticeTextView)

        if(talkRoomNoticeCount == 0)
            talkRoomNoticeTextView.visibility = View.GONE
        else
            talkRoomNoticeTextView.setText(talkRoomNoticeCount.toString(), TextView.BufferType.NORMAL)
    }
}