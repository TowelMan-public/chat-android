package towelman.server_on.net.chat_android.client.entity

/**
 * グループ加入者リストを取得するAPIのレスポンスとして返すエンティティー
 */
data class UserInGroupResponse(val talkRoomId: Int, val lastTalkIndex: Int,
                               val userIdName: String, val userName: String)