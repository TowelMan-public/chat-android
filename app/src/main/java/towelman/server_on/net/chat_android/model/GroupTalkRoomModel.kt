package towelman.server_on.net.chat_android.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

/**
 * グループトークルームのモデルクラス。
 */
class GroupTalkRoomModel(name: String, lastTalkIndex: Int, var groupTalkRoomId: Int, noticeCount: Int): TalkRoomModel(name, lastTalkIndex, noticeCount){
    constructor(): this("", -1, -1, -1)
}