package towelman.server_on.net.chat_android.client.entity

/**
 * グループ加入申請リストを取得するAPIのレスポンスとして返すエンティティー
 */
data class DesireUserInGroupResponse(val talkRoomId: Int, val lastTalkIndex: Int,
                                     val groupName: String)