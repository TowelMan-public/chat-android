package towelman.server_on.net.chat_android.handler

import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * 例外ハンドラーたちの集まり（List）であるExceptionHandlingListを、
 * コルーチンでのエラーハンドリングに特化させたクラス。
 */
class ExceptionHandlingListForCoroutine: ExceptionHandlingList() {
    /**
     * このクラスに登録されている例外ハンドラーたちで例外をハンドリングするための
     * コルーチン向けのハンドラーを生成する
     *
     * @return コルーチン向けの例外ハンドラー
     */
    fun createCoroutineExceptionHandler(): CoroutineExceptionHandler{
        return CoroutineExceptionHandler{_, throwable ->
            if(throwable is Exception)
                handlingAll(throwable)
            else
                handlingAll(Exception(throwable.message))
        }
    }

    operator fun plusAssign(exceptionHandlingList: ExceptionHandlingList){
        exceptionHandlerList.addAll(getExceptionHandlerList((exceptionHandlingList)))
    }

    operator fun plusAssign(exceptionHandler: ExceptionHandler){
        exceptionHandlerList.add(exceptionHandler)
    }
}