package towelman.server_on.net.chat_android.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

/**
 * 招待されているグループトークルームのモデルクラス。
 */
class DesireGroupTalkRoomModel(name: String, lastTalkIndex: Int, var groupTalkRoomId: Int, noticeCount: Int): TalkRoomModel(name, lastTalkIndex, noticeCount){
    constructor(): this("", -1, -1, -1)
}