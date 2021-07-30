package towelman.server_on.net.chat_android.updater

interface UpdaterInterface {
    /**
     * 更新を実行する
     *
     * @return 失敗したらfalseを返す
     */
    fun runUpdate(): Boolean
}