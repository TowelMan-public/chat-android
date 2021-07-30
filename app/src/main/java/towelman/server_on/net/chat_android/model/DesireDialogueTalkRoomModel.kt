package towelman.server_on.net.chat_android.model

/**
 * 追加申請が出てる友達トークルームのモデルクラス。
 */
class DesireDialogueTalkRoomModel(name: String, lastTalkIndex: Int, var haveUserIdName: String, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)