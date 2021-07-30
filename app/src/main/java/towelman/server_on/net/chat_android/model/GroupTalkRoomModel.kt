package towelman.server_on.net.chat_android.model

/**
 * グループトークルームのモデルクラス。
 */
class GroupTalkRoomModel(name: String, lastTalkIndex: Int, var groupTalkRoomId: Int, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)