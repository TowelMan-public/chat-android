package towelman.server_on.net.chat_android.client.entity

/**
 * グループトークルームを取得するAPIのエンティティー
 */
class GroupTalkRoomEntity(){
    var talkRoomId: Int = -1
    var groupName: String = ""
    var lastTalkIndex: Int = -1
    var isEnabled: Boolean = false

    constructor(talkRoomId: Int, groupName: String, lastTalkIndex: Int, isEnabled: Boolean) : this() {
        this.talkRoomId = talkRoomId
        this.groupName = groupName
        this.lastTalkIndex = lastTalkIndex
        this.isEnabled = isEnabled
    }
}