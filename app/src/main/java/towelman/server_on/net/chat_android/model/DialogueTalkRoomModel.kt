package towelman.server_on.net.chat_android.model

/**
 * 友達トークルームのモデルクラス。
 */
class DialogueTalkRoomModel(name: String, lastTalkIndex: Int, var haveUserIdName: String, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)