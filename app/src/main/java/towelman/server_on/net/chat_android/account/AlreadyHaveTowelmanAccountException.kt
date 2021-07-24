package towelman.server_on.net.chat_android.account

class AlreadyHaveTowelmanAccountException : RuntimeException(ERROR_MESSAGE) {
    companion object {
        const val ERROR_MESSAGE = "This already have 'TowelmanAccount'. So you can not add new 'TowelmanAccount' to this."
    }
}