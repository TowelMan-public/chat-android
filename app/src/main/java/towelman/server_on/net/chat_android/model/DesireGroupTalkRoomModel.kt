package towelman.server_on.net.chat_android.model

/**
 * 招待されているグループトークルームのモデルクラス。
 */
class DesireGroupTalkRoomModel(name: String, lastTalkIndex: Int, var groupTalkRoomId: Int, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)