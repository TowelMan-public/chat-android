package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.TalkResponse
import towelman.server_on.net.chat_android.client.exception.NotFoundException

import towelman.server_on.net.chat_android.client.exception.NotHaveUserException

/**
 * 友達トークに関するAPIを呼び出すクラス<br>
 * このクラスは部品の集まりとして使う。
 */
class DialogueApi
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/diarogue"
        private var restTemplate = RestTemplate.getInstance()

        /**
         * 友達トークリストの取得をするAPI
         * @param oauthToken 認証用トークン
         * @param userIdName ユーザーID名
         * @param maxSize 最大取得件数
         * @param startIndex 取得を開始するトークインデックス
         * @return 友達トークリスト
         * @throws NotFoundException
         * @throws NotHaveUserException
         */
        @Throws(NotFoundException::class, NotHaveUserException::class)
        fun getDialogueTalks(oauthToken: String, userIdName: String, maxSize: Int, startIndex: Int): List<TalkResponse> {
            val url = "$ROOT_URL/gets/talks"
            val dto = Dto(userIdName, maxSize, startIndex)
            return restTemplate.getListWhenLogined(oauthToken, url, dto, TalkResponse::class.java)
        }

        /**
         * 友達トークに関するAPIのパラメターを送るためのDtoクラス
         */
        class Dto(val userIdName: String?, val maxSize: Int?, val startIndex: Int?)
    }
}