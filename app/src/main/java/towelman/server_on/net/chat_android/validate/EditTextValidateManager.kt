package towelman.server_on.net.chat_android.validate

import java.util.*

/**
 * 1組のEditTextクラス向けのバリデーションチェックを管理するクラス
 */
class EditTextValidateManager {
    private val editTextValidatorList:  MutableList<EditTextValidator> = mutableListOf()



    /**
     *バリデーションチェックを追加する
     *
     * @param editTextValidator
     */
    fun add(editTextValidator: EditTextValidator){
        editTextValidatorList += mutableListOf(editTextValidator)
    }

    /**
     * 登録されている全てのバリデーションチェックを行う
     *
     * @return バリデーションチェックで引っかかったものがある場合はtrue, そうでない場合はfalseを返す
     */
    fun doValidateList(): Boolean{
        var haveValidateError = false

        editTextValidatorList.forEach{
            val validateMessage = it.runValidate()
            if(validateMessage != null)
                haveValidateError = true
        }

        return haveValidateError
    }
}