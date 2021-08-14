package towelman.server_on.net.chat_android.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

/**
 * 追加申請が出てる友達トークルームのモデルクラス。
 */
class DesireDialogueTalkRoomModel(name: String, lastTalkIndex: Int, var haveUserIdName: String, noticeCount: Int): TalkRoomModel(name, lastTalkIndex, noticeCount){
    constructor(): this("", -1, "", -1)
}