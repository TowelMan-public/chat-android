package towelman.server_on.net.chat_android.validate

/**
 * 1つの条件についてバリデーションチェックを実装・実行させるためのクラス。<br>
 * このクラスは継承・実装して使う。また、条件等で必要な情報等はコンストラクタに渡すことで実現する
 */
interface Validatable {
    /**
     * 1つの条件についてバリデーションチェックを実行する
     *
     * @param text バリデーションチェックしたい対象の文字列
     * @return エラーメッセージ。nullならバリデーションチェック成功
     */
    fun validate(text: String) : String?
}