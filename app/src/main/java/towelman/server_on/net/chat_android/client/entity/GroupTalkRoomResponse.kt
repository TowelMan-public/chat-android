package towelman.server_on.net.chat_android.client.entity

/**
 * グループを取得するAPIのレスポンスとして返すエンティティー
 */
class GroupTalkRoomResponse (var talkRoomId: Int, var groupName: String,
                             var groupLastTalkIndex: Int, var userLastTalkIndex: Int){
    constructor(): this(-1,"", -1, -1)
}