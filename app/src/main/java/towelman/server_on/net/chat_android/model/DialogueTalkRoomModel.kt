package towelman.server_on.net.chat_android.model

import org.parceler.Parcel

/**
 * 友達トークルームのモデルクラス。
 */
@Parcel
class DialogueTalkRoomModel(name: String, lastTalkIndex: Int, var haveUserIdName: String, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)