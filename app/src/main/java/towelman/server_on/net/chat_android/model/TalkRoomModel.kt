package towelman.server_on.net.chat_android.model

import org.parceler.Parcel

/**
 * トークルームのモデルクラス。
 * 実際にはこのクラスを継承したものを使う。
 */
@Parcel
open class TalkRoomModel(var name: String, var lastTalkIndex: Int, var noticeCount: Int)