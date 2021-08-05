package towelman.server_on.net.chat_android.model

import org.parceler.Parcel

/**
 * 追加申請が出てる友達トークルームのモデルクラス。
 */
@Parcel
class DesireDialogueTalkRoomModel(name: String, lastTalkIndex: Int, var haveUserIdName: String, noticeCount: Int):
        TalkRoomModel(name, lastTalkIndex, noticeCount)