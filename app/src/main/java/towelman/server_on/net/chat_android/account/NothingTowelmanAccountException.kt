package towelman.server_on.net.chat_android.account

class NothingTowelmanAccountException : RuntimeException(ERROR_MESSAGE) {
    companion object {
        const val ERROR_MESSAGE = "This have no 'TowelmanAccount'. So you can not get 'TowelmanAccount' in this."
    }
}