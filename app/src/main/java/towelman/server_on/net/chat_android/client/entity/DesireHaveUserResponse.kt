package towelman.server_on.net.chat_android.client.entity

/**
 * 友達追加申請リストを取得するAPIのレスポンスとして返すエンティティー
 */
data class DesireHaveUserResponse(val haveUserIdName: String, val haveUserName: String,
                                  val talkRoomId: Int, val lastTalkIndex: Int)