package towelman.server_on.net.chat_android.model

import android.os.Parcelable
import org.parceler.Parcel

/**
 * トークのモデルクラス
 */
class TalkModel(val talkIndex: Int, val SenderUserName: String, val isMyTalk: Boolean, var contentText: String, val timestamp: String)