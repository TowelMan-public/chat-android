package towelman.server_on.net.chat_android.validate

/**
 * 「空白でない」という条件を満たすかをバリデーションチェックするクラス
 */
class NotBlankValidatable: Validatable {
    /**
     * 1つの条件についてバリデーションチェックを実行する
     *
     * @param text バリデーションチェックしたい対象の文字列
     * @return エラーメッセージ。nullならバリデーションチェック成功
     */
    override fun validate(text: String) : String?{
        return if(text.isBlank())
            "何か入力してください。空白はだめです。"
        else
            null
    }
}