package towelman.server_on.net.chat_android.validate

import android.widget.EditText

/**
 * 1つのEditTextに対してバリデーションチェックを行うクラス
 */
class EditTextValidator(val editTextView: EditText) {
    //バリデーションチェックの項目たち
    private val validatableList:  MutableList<Validatable> = mutableListOf()

    /**
     * バリデーションチェックの項目を追加する
     *
     * @param validatable バリデーションチェックの項目
     */
    fun addValidatable(validatable: Validatable){
        validatableList += mutableListOf(validatable)
    }

    /**
     * バリデーションチェックを、指定されたすべての項目について実行する
     *
     * @return バリデーションチェックのメッセージ。　全て成功ならnull。
     */
    fun runValidate(): String?{
        var validateMessage:String? = null;
        run loop@ {
            validatableList.forEach {
                validateMessage = it.validate(editTextView.text.toString())
                if (validateMessage != null)
                    return@loop
            }
        }

        editTextView.error = validateMessage
        return validateMessage;
    }
}