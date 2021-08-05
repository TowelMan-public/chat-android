package towelman.server_on.net.chat_android.service

import towelman.server_on.net.chat_android.client.api.*
import towelman.server_on.net.chat_android.model.TalkModel

/**
 * APIを使う、グループに関する処理を書くところ<br>
 * このクラスは部品の集まりとして使う。
 */
class TalkRestService /**
 * このクラスを生成不能にするためのコンストラクタ
 */
private constructor(){
    companion object {
        /**
         * 友達トークリストの取得
         *
         * @param oauthToken 認証用トークン
         * @param myUserIdName 自分のユーザーID名
         * @param haveUserIdName 友達のユーザーID名
         * @param startIndex 取得開始するインデックス
         * @param maxSize 最大取得件数
         * @return 友達トークモデルリスト
         */
        fun getDialogueTalkList(oauthToken: String, myUserIdName: String, haveUserIdName: String, startIndex: Int, maxSize: Int): MutableList<TalkModel>{
            val entityList = DialogueApi.getDialogueTalks(oauthToken, haveUserIdName, maxSize, startIndex)
            val modelList: MutableList<TalkModel> = mutableListOf()

            entityList.forEach{
                modelList.add(
                    TalkModel(it.talkIndex, it.userName, it.userIdName == myUserIdName, it.content, it.timestampString)
                )
            }

            return modelList
        }

        /**
         * グループトークリストの取得
         *
         * @param oauthToken 認証用トークン
         * @param myUserIdName 自分のユーザーID名
         * @param groupTalkRoomId グループトークルームID
         * @param startIndex 取得開始するインデックス
         * @param maxSize 最大取得件数
         * @return グループトークモデルリスト
         */
        fun getGroupTalkList(oauthToken: String, myUserIdName: String, groupTalkRoomId: Int, startIndex: Int, maxSize: Int): MutableList<TalkModel>{
            val entityList = GroupApi.getGroupTalks(oauthToken, groupTalkRoomId, maxSize, startIndex)
            val modelList: MutableList<TalkModel> = mutableListOf()

            entityList.forEach{
                modelList.add(
                    TalkModel(it.talkIndex, it.userName, it.userIdName == myUserIdName, it.content, it.timestampString)
                )
            }

            return modelList
        }

        /**
         * 友達トークを送信する
         *
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達のユーザーID名
         * @param contentText トークの内容
         */
        fun sendDialogueTalk(oauthToken: String, haveUserIdName: String, contentText: String){
            DialogueTalkApi.insertTalk(oauthToken, haveUserIdName, contentText)
        }

        /**
         * 友達トークを削除する
         *
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達のユーザーID名
         * @param talkIndex トークインデックス
         */
        fun deleteDialogueTalk(oauthToken: String, haveUserIdName: String, talkIndex: Int){
            DialogueTalkApi.deleteTalk(oauthToken, haveUserIdName, talkIndex)
        }

        /**
         * 友達トークを変更する
         *
         * @param oauthToken 認証用トークン
         * @param haveUserIdName 友達のユーザーID名
         * @param talkIndex トークインデックス
         * @param contentText トークの内容
         */
        fun changeDialogueTalk(oauthToken: String, haveUserIdName: String, talkIndex: Int, contentText: String){
            DialogueTalkApi.updateTalk(oauthToken, haveUserIdName, talkIndex, contentText)
        }

        /**
         * グループトークを送信する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param contentText トークの内容
         */
        fun sendGroupTalk(oauthToken: String, groupTalkRoomId: Int, contentText: String){
            GroupTalkApi.insertTalk(oauthToken, groupTalkRoomId, contentText)
        }

        /**
         * グループトークを削除する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param talkIndex トークインデックス
         */
        fun deleteGroupTalk(oauthToken: String, groupTalkRoomId: Int, talkIndex: Int){
            GroupTalkApi.deleteTalk(oauthToken, groupTalkRoomId, talkIndex)
        }

        /**
         * グループトークを変更する
         *
         * @param oauthToken 認証用トークン
         * @param groupTalkRoomId グループトークルームID
         * @param talkIndex トークインデックス
         * @param contentText トークの内容
         */
        fun changeGroupTalk(oauthToken: String, groupTalkRoomId: Int, talkIndex: Int, contentText: String){
            GroupTalkApi.updateTalk(oauthToken, groupTalkRoomId, talkIndex, contentText)
        }
    }
}