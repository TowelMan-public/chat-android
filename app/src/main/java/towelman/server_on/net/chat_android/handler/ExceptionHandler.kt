package towelman.server_on.net.chat_android.handler

/**
 * 例外ハンドラー（単体）<br>
 * 1つの例外クラスに対する処理を定義し、実行させるクラスである<br>
 * コンストラクタの型引数に補足したい例外クラスを、その時に行いたい処理をラムダで引数に指定する
 */
 class ExceptionHandler<E: Exception>(val handler: (exception: E)-> Unit): ExceptionHandlerInterface {

    /**
     * 例外処理を実行する
     *
     * @param exception ハンドリングしたい、Exceptionに抽象化された例外
     * @return このハンドラーで適切に処理されたかどうか。trueなら適切に処理されている。falseなら他のハンドラーを実行する必要がある
     */
    override fun handling(exception: Exception): Boolean {
        //未検査キャストと警告が出るが問題ない
        val eException = exception as E?

        return if (eException == null)
            false
        else {
            handler(eException)
            true
        }
    }
}