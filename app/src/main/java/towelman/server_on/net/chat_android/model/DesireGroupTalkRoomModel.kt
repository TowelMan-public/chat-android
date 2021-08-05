package towelman.server_on.net.chat_android.model

import org.parceler.Parcel

/**
 * 招待されているグループトークルームのモデルクラス。
 */
@Parcel
class DesireGroupTalkRoomModel(name: String, lastTalkIndex: Int, var groupTalkRoomId: Int, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)