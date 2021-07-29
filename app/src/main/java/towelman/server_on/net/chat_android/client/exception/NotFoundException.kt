package towelman.server_on.net.chat_android.client.exception

import java.util.regex.Pattern

class NotFoundException(message: String) : RuntimeException(message) {

    private fun getErrorFieldName(): String {
        //正規表現のパターン
        val patternString = "^(.*) is not found$"

        //項目（ここではfieldName）を取得する
        val matcher = Pattern.compile(patternString)
                .matcher(super.message.toString())
        matcher.matches()
        return matcher.group(1)!!
    }

    fun isErrorFieldUserIdName(): Boolean {
        return getErrorFieldName() == "userIdName"
    }
}