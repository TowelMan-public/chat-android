package towelman.server_on.net.chat_android.model

import org.parceler.Parcel

/**
 * グループトークルームのモデルクラス。
 */
@Parcel
class GroupTalkRoomModel(name: String, lastTalkIndex: Int, var groupTalkRoomId: Int, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)