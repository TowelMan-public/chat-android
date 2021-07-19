package towelman.server_on.net.chat_android.client.entity

/**
 * ユーザー情報を表すエンティティー
 */
data class UserEntity(val userId: Int, val userIdName: String,
                        val userName: String, val password: String)