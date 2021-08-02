package towelman.server_on.net.chat_android.updater

import kotlinx.coroutines.*
import towelman.server_on.net.chat_android.handler.ExceptionHandlingList

/**
 * 1つのデータに対する更新処理をするクラス
 */
open class Updater<T>: UpdaterInterface{
    /**
     * 更新される対象のデータ<br>
     * updateDelegateを説明の通りに実装していれば最後に更新されたときのデータを取得することができる
     */
    var model: T? = null
        private set

    /**
     * 例外ハンドリングを含まない更新処理をセット・取得する<br>
     * 更新結果（求めていたデータ）を戻り値として返してやると良い。
     */
    var updateDelegate: (() -> T?) = {null}

    /**
     * 更新処理に成功したときに実行する処理たちのセット・取得<br>
     * 更新結果（求めていたデータ）が引数として渡されるので適宜それを使う。<br>
     * また、Mapにしてあるが、その理由は更新に成功したのをトリガーに実行したい処理を複数の場所からその結果を使って実行できるようにするためである。
     * Key名は「クラス名（フルパス付）」とかにするといいだろう
     */
    val successDelegateList: MutableMap<String, (model: T?) -> Unit> = mutableMapOf()

    /**
     * 更新処理に失敗したときに実行する例外ハンドリングリストである。更新処理(updateDelegate)に
     * 例外ハンドリングを含まないと指定した理由はここで指定してほしかったからである。
     */
    var exceptionHandlingList: ExceptionHandlingList = ExceptionHandlingList()

    /**
     * 更新を実行する
     *
     * @return 失敗したらfalseを返す
     */
    override fun runUpdate(): Boolean{
        var isSuccess = true
        CoroutineScope(Dispatchers.Main + Job()).launch() {
            try {
                withContext(Dispatchers.Default) {
                    model = updateDelegate()
                }
                successDelegateList.forEach {
                    it.value(model)
                }
            }
            catch (e: Exception){
                exceptionHandlingList.handlingAll(e)
                isSuccess = false
            }
        }
        return isSuccess
    }
}