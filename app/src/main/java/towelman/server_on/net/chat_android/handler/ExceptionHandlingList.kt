package towelman.server_on.net.chat_android.handler

/**
 * 例外ハンドラーたちの集まり（List）<br>
 * また、Exceptionに抽象化された例外を元に各登録されている例外ハンドラーたちでハンドリングするクラス。<br>
 * 最後に登録されたやつからハンドリングを実行していく
 */
open class ExceptionHandlingList {
    private val exceptionHandlerList: MutableList<ExceptionHandlerInterface> = mutableListOf()

    /**
     * 例外ハンドラーを単体で追加する
     */
    fun add(exceptionHandler: ExceptionHandlerInterface){
        exceptionHandlerList += mutableListOf(exceptionHandler)
    }

    /**
     * 例外ハンドラーをListで追加する
     */
    fun add(exceptionHandlingList: ExceptionHandlingList){
        exceptionHandlerList += exceptionHandlingList.exceptionHandlerList
    }

    /**
     * このクラスに登録されている例外ハンドラーたちで例外をハンドリングする
     *
     * @param exception ハンドリングしたい、Exceptionに抽象化された例外
     */
    fun handlingAll(exception: Exception){
        exceptionHandlerList.reverse()
        run loop@{
            exceptionHandlerList.forEach {
                if (it.handling(exception))
                    return@loop
            }
        }
    }

    operator fun plus(exceptionHandler: ExceptionHandlerInterface): ExceptionHandlingList{
        val newExceptionHandlingList = ExceptionHandlingList()
        newExceptionHandlingList.add(this)
        newExceptionHandlingList.add(exceptionHandler)
        return newExceptionHandlingList
    }

    operator fun plus(exceptionHandlingList: ExceptionHandlingList): ExceptionHandlingList{
        val newExceptionHandlingList = ExceptionHandlingList()
        newExceptionHandlingList.add(this)
        newExceptionHandlingList.add(exceptionHandlingList)
        return newExceptionHandlingList
    }

    operator fun plusAssign(exceptionHandler: ExceptionHandlerInterface){
        add(exceptionHandler)
    }

    operator fun plusAssign(exceptionHandlingList: ExceptionHandlingList){
        add(exceptionHandlingList)
    }
}