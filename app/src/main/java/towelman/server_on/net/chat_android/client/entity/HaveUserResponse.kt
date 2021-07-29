package towelman.server_on.net.chat_android.client.entity

/**
 * 友達リストを取得するAPIのレスポンスとして返すエンティティー
 */
class HaveUserResponse(var haveUserIdName: String, var haveUserName: String,
                       var talkRoomId: Int, var talkLastIndex: Int,
                       var myLastTalkIndex: Int){
    constructor(): this("", "", -1, -1, -1)
}