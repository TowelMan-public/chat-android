package towelman.server_on.net.chat_android.account

/**
 * このアプリが使うアカウントがまだこの機種に登録されていないときに投げられる例外
 */
class NothingTowelmanAccountException : RuntimeException(ERROR_MESSAGE) {
    companion object {
        const val ERROR_MESSAGE = "This have no 'TowelmanAccount'. So you can not get 'TowelmanAccount' in this."
    }
}