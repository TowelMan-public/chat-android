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
class TalkRoomView(context : Context) : LinearLayout(context) {
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
        findViewById<TextView>(R.id.talkRoomNoticeTextView).setText(talkRoomNoticeCount.toString(), TextView.BufferType.NORMAL)
    }
}