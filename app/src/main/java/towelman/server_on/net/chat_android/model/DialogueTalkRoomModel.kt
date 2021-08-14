package towelman.server_on.net.chat_android.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

/**
 * 友達トークルームのモデルクラス。
 */
class DialogueTalkRoomModel(name: String, lastTalkIndex: Int, var haveUserIdName: String, noticeCount: Int): TalkRoomModel(name, lastTalkIndex, noticeCount){
    constructor(): this("", -1, "", -1)
}