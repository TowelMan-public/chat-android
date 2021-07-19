package towelman.server_on.net.chat_android.client.entity

/**
 * 友達リストを取得するAPIのレスポンスとして返すエンティティー
 */
data class HaveUserResponse(val haveUserIdName: String, val haveUserName: String,
                            val talkRoomId: Int, val talkLastIndex: Int,
                            val myLastTalkIndex: Int)