package towelman.server_on.net.chat_android.handler

import java.util.*

/**
 * 例外ハンドラーたちの集まり（List）<br>
 * また、Exceptionに抽象化された例外を元に各登録されている例外ハンドラーたちでハンドリングするクラス。<br>
 * 最後に登録されたやつからハンドリングを実行していく
 */
open class ExceptionHandlingList {
    protected val exceptionHandlerList: MutableList<ExceptionHandlerInterface> = mutableListOf()

    protected fun getExceptionHandlerList(exceptionHandlingList: ExceptionHandlingList)
        = exceptionHandlingList.exceptionHandlerList

    /**
     * 例外ハンドラーを単体で追加する
     *
     * @param exceptionHandler 例外ハンドラー
     */
    fun add(exceptionHandler: ExceptionHandlerInterface){
        exceptionHandlerList += mutableListOf(exceptionHandler)
    }

    /**
     * 例外ハンドラーをListで追加する
     *
     * @param exceptionHandlingList 例外ハンドラーのListクラス（ExceptionHandlingList）
     */
    fun add(exceptionHandlingList: ExceptionHandlingList){
        for (it in exceptionHandlingList.exceptionHandlerList) {
            exceptionHandlerList += exceptionHandlingList.exceptionHandlerList
        }
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

    /**
     * +演算子のオーバーロード
     *
     * @param exceptionHandler エラーハンドラー
     * @return 例外ハンドラーのListクラス（ExceptionHandlingList）
     */
    operator fun plus(exceptionHandler: ExceptionHandlerInterface): ExceptionHandlingList{
        val newExceptionHandlingList = ExceptionHandlingList()
        newExceptionHandlingList.add(this)
        newExceptionHandlingList.add(exceptionHandler)
        return newExceptionHandlingList
    }

    /**
     * +演算子のオーバーロード
     *
     * @param exceptionHandlingList 例外ハンドラーのListクラス（ExceptionHandlingList）
     * @return 例外ハンドラーのListクラス（ExceptionHandlingList）
     */
    operator fun plus(exceptionHandlingList: ExceptionHandlingList): ExceptionHandlingList{
        val newExceptionHandlingList = ExceptionHandlingList()
        newExceptionHandlingList.add(this)
        newExceptionHandlingList.add(exceptionHandlingList)
        return newExceptionHandlingList
    }
}