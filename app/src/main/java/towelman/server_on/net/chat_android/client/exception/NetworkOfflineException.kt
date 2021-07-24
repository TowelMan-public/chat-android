package towelman.server_on.net.chat_android.client.exception

import java.io.IOException

class NetworkOfflineException : IOException() {
    override val message: String?
        get() = "This is not connected to network"
}