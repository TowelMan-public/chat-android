package towelman.server_on.net.chat_android.handler

 class ExceptionHandler<E: Exception>(val handler: (exception: E)-> Unit): ExceptionHandlerInterface {
    override fun handling(exception: Exception): Boolean {
        //未検査キャストと警告が出るが問題ない
        val eException = exception as E?

        return if (exception == null)
            false
        else {
            handler(exception)
            true
        }
    }
}