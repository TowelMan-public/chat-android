package towelman.server_on.net.chat_android.validate

import android.widget.EditText

/**
 * 「指定したEditTextの文字列と等しい」という条件を満たすかをバリデーションチェックするクラス
 */
class EqualEditTextValidatable(val editText: EditText) : Validatable {
    /**
     * 1つの条件についてバリデーションチェックを実行する
     *
     * @param text バリデーションチェックしたい対象の文字列
     * @return エラーメッセージ。nullならバリデーションチェック成功
     */
    override fun validate(text: String) : String?{
        return if(text != editText.text.toString())
            "一致しません。もう一度お確かめください"
        else
            null
    }
}