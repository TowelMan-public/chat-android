package towelman.server_on.net.chat_android.handler

/**
 * 例外ハンドラーのインターフェイス。
 * 例外ハンドラーを登録する以外は基本このインターフェイスで扱う。
 */
interface ExceptionHandlerInterface {

    /**
     * 例外処理を実行する
     *
     * @param exception ハンドリングしたい、Exceptionに抽象化された例外
     * @return このハンドラーで適切に処理されたかどうか。trueなら適切に処理されている。falseなら他のハンドラーを実行する必要がある
     */
    fun handling(exception: Exception): Boolean

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