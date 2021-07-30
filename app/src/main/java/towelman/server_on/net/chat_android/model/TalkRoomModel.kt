package towelman.server_on.net.chat_android.model

/**
 * トークルームのモデルクラス。
 * 実際にはこのクラスを継承したものを使う。
 */
open class TalkRoomModel(var name: String, var lastTalkIndex: Int, var noticeCount: Int)