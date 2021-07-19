package towelman.server_on.net.chat_android.client.exception

class AlreadyHaveUserException: RuntimeException(ERROR_MESSAGE) {
    companion object {
        const val ERROR_MESSAGE = "This user is already inserted in dialogue by you"
    }
}