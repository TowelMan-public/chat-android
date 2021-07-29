package towelman.server_on.net.chat_android.client.entity

/**
 * グループ加入者リストを取得するAPIのレスポンスとして返すエンティティー
 */
class UserInGroupResponse(var talkRoomId: Int, var lastTalkIndex: Int,
                          var userIdName: String, var userName: String){
    constructor(): this(-1, -1, "", "")
}