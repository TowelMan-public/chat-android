package towelman.server_on.net.chat_android.client.entity

/**
 * トークを取得するAPIのレスポンスとして返すエンティティー
 */
data class TalkResponse(val talkIndex: Int, val userIdName: String,
                        val userName: String, val content: String,
                        val timestampString:String)