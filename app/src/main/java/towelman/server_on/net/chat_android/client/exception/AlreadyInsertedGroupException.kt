package towelman.server_on.net.chat_android.client.exception

class AlreadyInsertedGroupException: RuntimeException(ERROR_MESSAGE) {
    companion object {
        const val ERROR_MESSAGE = "User is already inserted in this group!"
    }
}