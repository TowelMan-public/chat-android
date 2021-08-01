package towelman.server_on.net.chat_android.updater

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed

/**
 * 更新処理を行う(Updater)クラスを管理するクラスである。<br>
 * このクラスのインスタンスを取得するときにはgetInstanceを呼んで取得すること
 * （このクラスは1つのアプリ内で共有されるようにしたいからである。）
 */
class UpdateManager {
    private val handler = Handler(Looper.getMainLooper())
    private val updaterMap: MutableMap<String, UpdaterInterface> = mutableMapOf()
    private val optionMap: MutableMap<String, Option> = mutableMapOf()

    /**
     * 指定したUpdaterが登録されているかどうか<br>
     * 登録されていればtrue、登録されていなければfalseを返す
     *
     * @param updateKey 更新処理を行う(Updater)クラスを紐づける文字列
     */
    fun isEnableUpdater(updateKey: String) = updaterMap[updateKey] != null

    /**
     * 更新処理を行う(Updater)クラスの追加
     *
     * @param updateKey 更新処理を行う(Updater)クラスを紐づける文字列
     * @param updater　更新処理を行う(Updater)クラス
     */
    fun addUpdater(updateKey: String, updater: UpdaterInterface){
        updaterMap[updateKey] = updater
        optionMap[updateKey] = Option()
    }

    /**
     * 更新処理を行う(Updater)クラスの取得
     *
     * @param updateKey 更新処理を行う(Updater)クラスを紐づける文字列
     * @return 更新処理を行う(Updater)クラス
     */
    fun getUpdater(updateKey: String) = updaterMap[updateKey]!!

    /**
     * updateKeyに紐づけられた更新処理を行う(Updater)クラスの更新を行う間隔を指定する
     * 指定しない（定期実行しない）場合は-1を指定する
     *
     * @param updateKey 更新処理を行う(Updater)クラスを紐づける文字列
     * @param updateTimeSpan 更新を行う間隔（ミリ秒）
     */
    fun setUpdateTimeSpan(updateKey: String, updateTimeSpan: Long){
        optionMap[updateKey]!!.updateTimeSpan = updateTimeSpan

        handler.removeCallbacks(optionMap[updateKey]!!.runnable)

        //-1が指定されてるときは実行しない
        if(updateTimeSpan == (-1).toLong())
            return

        optionMap[updateKey]!!.runnable = object : Runnable {
            override fun run() {
                if (updaterMap[updateKey]!!.runUpdate())
                    handler.postDelayed(this, updateTimeSpan)
                else{
                    handler.removeCallbacks(this)
                    optionMap[updateKey]!!.hasError = true
                }
            }
        }
        handler.post(optionMap[updateKey]!!.runnable)
    }

    /**
     * updateKeyに紐づけられた更新処理を行う(Updater)クラスの更新を行う間隔を取得する
     *
     * @param updateKey 更新処理を行う(Updater)クラスを紐づける文字列
     * @return updateKeyに紐づけられた更新処理を行う(Updater)クラスの更新を行う間隔(ミリ秒)、指定されてなかったら-1
     */
    fun getUpdateTimeSpan(updateKey: String) = optionMap[updateKey]!!.updateTimeSpan

    /**
     * updateKeyに紐づけられた更新処理を行う(Updater)クラスをこのクラスから削除する
     *
     * @param updateKey 更新処理を行う(Updater)クラスを紐づける文字列
     */
    fun deleteUpdater(updateKey: String){
        handler.removeCallbacks(optionMap[updateKey]!!.runnable)
        optionMap.remove(updateKey)
        updaterMap.remove(updateKey)
    }

    /**
     * 更新処理を行う(Updater)クラスをこのクラスから全て削除する
     */
    fun deleteUpdaterAll(){
        optionMap.forEach{
            handler.removeCallbacks(it.value.runnable)
            optionMap.remove(it.key)
            updaterMap.remove(it.key)
        }
    }

    companion object{
        @JvmStatic
        private val updateManager: UpdateManager = UpdateManager()

        /**
         * このクラスのインスタンスを取得する
         */
        @JvmStatic
        fun getInstance() = updateManager
    }

    /**
     * このクラス内で、1つの更新処理を行う(Updater)クラスに対する情報等を保持するインナークラス
     */
    private class Option(){
        var updateTimeSpan: Long = -1
        var hasError: Boolean = false
        var runnable: Runnable = Runnable{}
    }
}