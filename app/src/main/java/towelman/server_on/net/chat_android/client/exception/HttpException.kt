package towelman.server_on.net.chat_android.client.exception

class HttpException (message: String, val httpStatusCode: Int): RuntimeException(message) {
}