package towelman.server_on.net.chat_android.handler

interface ExceptionHandlerInterface {
    fun handling(exception: Exception): Boolean

    operator fun plus(exceptionHandler: ExceptionHandlerInterface): ExceptionHandlingList{
        val newExceptionHandlingList = ExceptionHandlingList();
        newExceptionHandlingList.add(this)
        newExceptionHandlingList.add(exceptionHandler)
        return newExceptionHandlingList
    }

    operator fun plus(exceptionHandlingList: ExceptionHandlingList): ExceptionHandlingList{
        val newExceptionHandlingList = ExceptionHandlingList();
        newExceptionHandlingList.add(this)
        newExceptionHandlingList.add(exceptionHandlingList)
        return newExceptionHandlingList
    }
}