package towelman.server_on.net.chat_android.client.entity

/**
 * グループを取得するAPIのレスポンスとして返すエンティティー
 */
data class GroupTalkRoomResponse (val talkRoomId: Int, val groupName: String,
                                  val groupLastTalkIndex: Int, val userLastTalkIndex: Int)