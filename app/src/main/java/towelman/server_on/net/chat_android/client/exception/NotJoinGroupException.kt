package towelman.server_on.net.chat_android.client.exception

class NotJoinGroupException : RuntimeException(ERROR_MESSAGE) {
    companion object{
        const val ERROR_MESSAGE = "This user is not inserted group"
    }
}