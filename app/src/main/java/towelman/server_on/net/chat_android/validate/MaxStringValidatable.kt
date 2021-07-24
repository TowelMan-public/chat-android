package towelman.server_on.net.chat_android.validate

/**
 * 「指定された長さ以下に文字列が収まっている」という条件を満たすかをバリデーションチェックするクラス
 */
class MaxStringValidatable(val maxLength: Int) : Validatable {
    /**
     * 1つの条件についてバリデーションチェックを実行する
     *
     * @param text バリデーションチェックしたい対象の文字列
     * @return エラーメッセージ。nullならバリデーションチェック成功
     */
    override fun validate(text: String) : String?{
        return if(text.length > maxLength)
            maxLength.toString() + "文字以内に収めてください"
        else
            null
    }
}