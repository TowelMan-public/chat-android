package towelman.server_on.net.chat_android.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

/**
 * トークルームのモデルクラス。
 * 実際にはこのクラスを継承したものを使う。
 */
open class TalkRoomModel(var name: String, var lastTalkIndex: Int, var noticeCount: Int)