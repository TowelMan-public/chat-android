package towelman.server_on.net.chat_android.client.entity

/**
 * トークを取得するAPIのレスポンスとして返すエンティティー
 */
class TalkResponse(var talkIndex: Int, var userIdName: String,
                   var userName: String, var content: String,
                   var timestampString:String){
    constructor(): this(-1, "", "", "", "")
}