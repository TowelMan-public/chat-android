package towelman.server_on.net.chat_android.client.entity

/**
 * ユーザー情報を表すエンティティー
 */
class UserEntity(var userId: Int, var userIdName: String,
                 var userName: String, var password: String){
    constructor(): this(-1, "", "", "")
}