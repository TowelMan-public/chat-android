package towelman.server_on.net.chat_android.account

/**
 * 既にこの機種に、このアプリで使うアカウントが登録されているときに投げられる例外
 */
class AlreadyHaveTowelmanAccountException : RuntimeException(ERROR_MESSAGE) {
    companion object {
        const val ERROR_MESSAGE = "This already have 'TowelmanAccount'. So you can not add new 'TowelmanAccount' to this."
    }
}