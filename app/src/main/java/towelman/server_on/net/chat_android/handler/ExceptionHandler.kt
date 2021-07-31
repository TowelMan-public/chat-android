package towelman.server_on.net.chat_android.handler

import java.lang.reflect.ParameterizedType

/**
 * 例外ハンドラー（単体）<br>
 * 1つの例外クラスに対する処理を定義し、実行させるクラスである<br>
 * newIncenseの型引数に補足したい例外クラスを、その時に行いたい処理をラムダで引数に指定して生成する<br>
 * コンストラクタはアクセスできてしまうがコンストラクタをあまり使わないようにすること
 */
class ExceptionHandler constructor(val runDelegate: (exception: Exception) -> Boolean) : ExceptionHandlerInterface {
    /**
     * 例外処理を実行する
     *
     * @param exception ハンドリングしたい、Exceptionに抽象化された例外
     * @return このハンドラーで適切に処理されたかどうか。trueなら適切に処理されている。falseなら他のハンドラーを実行する必要がある
     */
    override fun handling(exception: Exception): Boolean {
        return runDelegate(exception)
    }

    companion object{
        /**
         * 例外ハンドラー（単体）を生成する
         *
         * @param E 型引数に補足したい例外クラス
         * @param runDelegate 行いたい処理（対象の例外が引数に渡される）
         * @return 例外ハンドラー（単体）
         */
        @JvmStatic
        inline fun <reified E> newIncense(crossinline runDelegate: (exception: E) -> Unit): ExceptionHandler{
            return ExceptionHandler {
                    if(it is E) {
                        try {
                            runDelegate(it)
                            true
                        }
                        catch (_: Exception){
                            false
                        }
                    }
                    else
                        false
            }
        }
    }
}