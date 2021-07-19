package towelman.server_on.net.chat_android.client.exception

class AlreadyUsedUserIdNameException: RuntimeException(ERROR_MESSAGE) {
    companion object {
        const val ERROR_MESSAGE = "This userIdName is already used"
    }
}