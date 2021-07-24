package towelman.server_on.net.chat_android.handler

import kotlinx.coroutines.CoroutineExceptionHandler

class ExceptionHandlingListForCoroutine: ExceptionHandlingList() {
    fun createCoroutineExceptionHandler(): CoroutineExceptionHandler{
        return CoroutineExceptionHandler{_, throwable ->
            if(throwable is Exception)
                handlingAll(throwable)
            else
                handlingAll(Exception(throwable.message))
        }
    }
}