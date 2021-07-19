package towelman.server_on.net.chat_android.client.entity

/**
 * グループトークルームを取得するAPIのエンティティー
 */
data class GroupTalkRoomEntity(val talkRoomId: Int, val groupName: String,
                               val lastTalkIndex: Int, val isEnabled: Boolean)