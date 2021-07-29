package towelman.server_on.net.chat_android.client.entity

/**
 * 友達追加申請リストを取得するAPIのレスポンスとして返すエンティティー
 */
class DesireHaveUserResponse() {
    var haveUserIdName: String = ""
    var haveUserName: String = ""
    var talkRoomId: Int = -1
    var lastTalkIndex: Int = -1

    constructor(haveUserIdName: String, haveUserName: String, talkRoomId: Int, lastTalkIndex: Int) : this() {
        this.haveUserIdName = haveUserIdName
        this.haveUserName = haveUserName
        this.talkRoomId = talkRoomId
        this.lastTalkIndex = lastTalkIndex
    }
}