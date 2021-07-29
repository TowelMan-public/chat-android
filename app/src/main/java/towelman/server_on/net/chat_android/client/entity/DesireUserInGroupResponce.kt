package towelman.server_on.net.chat_android.client.entity

/**
 * グループ加入申請リストを取得するAPIのレスポンスとして返すエンティティー
 */
class DesireUserInGroupResponse(){
    var talkRoomId: Int = -1
    var lastTalkIndex: Int = -1
    var groupName: String = ""

    constructor(talkRoomId: Int, lastTalkIndex: Int, groupName: String) : this() {
        this.talkRoomId = talkRoomId
        this.lastTalkIndex = lastTalkIndex
        this.groupName = groupName
    }
}