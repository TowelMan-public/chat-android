package towelman.server_on.net.chat_android.client.api

import towelman.server_on.net.chat_android.client.RestTemplate
import towelman.server_on.net.chat_android.client.entity.TalkResponse
import towelman.server_on.net.chat_android.client.exception.BadRequestFormException
import towelman.server_on.net.chat_android.client.exception.NotFoundException
import towelman.server_on.net.chat_android.client.exception.NotHaveUserException


/**
 * 友達トーク単体に関するAPIを呼び出すクラス<br>
 * このクラスは部品の集まりとして使う。
 */
class DialogueTalkApi
/**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor() {

    companion object {
        private const val ROOT_URL: String = ApiUrlRootConfig.ROOT_URL + "/dialogue/talk"
        private var restTemplate = RestTemplate.getInstance()

        /**
         * 友達トークの作成をするAPI
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達ユーザーのID名
         * @param talkContentText トークの内容
         * @throws NotFoundException
         * @throws NotHaveUserException
         */
        @Throws(NotFoundException::class, NotHaveUserException::class)
        fun insertTalk(oauthToken: String, haveUserIdName: String, talkContentText: String) {
            val url = "$ROOT_URL/insert"
            val dto = Dto(haveUserIdName, talkContentText, null)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * 友達トークの取得をするAPI
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達ユーザーのID名
         * @param talkIndex　トークインデックス
         * @return 友達トーク
         * @throws NotFoundException
         * @throws NotHaveUserException
         */
        @Throws(NotFoundException::class, NotHaveUserException::class)
        fun getTalk(oauthToken: String, haveUserIdName: String, talkIndex: Int): TalkResponse {
            val url = "$ROOT_URL/get"
            val dto = Dto(haveUserIdName, null, talkIndex)
            return restTemplate.getWhenLogined(oauthToken, url, dto)
        }

        /**
         * 友達トークの更新をするAPI
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達ユーザーのID名
         * @param talkIndex トークインデックス
         * @param talkContentText トークテキスト
         * @throws NotFoundException
         * @throws NotHaveUserException
         * @throws BadRequestFormException
         */
        @Throws(NotFoundException::class, NotHaveUserException::class, BadRequestFormException::class)
        fun updateTalk(oauthToken: String, haveUserIdName: String, talkIndex: Int, talkContentText: String) {
            val url = "$ROOT_URL/update"
            val dto = Dto(haveUserIdName, talkContentText, talkIndex)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * 友達トークの削除をするAPI
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達ユーザーのID名
         * @param talkIndex トークインデックス
         * @throws NotFoundException
         * @throws NotHaveUserException
         * @throws BadRequestFormException
         */
        @Throws(NotFoundException::class, NotHaveUserException::class, BadRequestFormException::class)
        fun deleteTalk(oauthToken: String, haveUserIdName: String, talkIndex: Int) {
            val url = "$ROOT_URL/delete"
            val dto = Dto(haveUserIdName, null, talkIndex)
            restTemplate.postWhenLogined(oauthToken, url, dto)
        }

        /**
         * 友達トーク単体に関するAPIのパラメターを送るためのDtoクラス
         */
        class Dto(val haveUserIdName: String?, val talkContentText: String?, val talkIndex: Int?)
    }
}