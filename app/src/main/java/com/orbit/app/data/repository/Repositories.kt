package com.orbit.app.data.repository
import com.orbit.app.data.model.HeartPulse
import com.orbit.app.data.model.Message
import com.orbit.app.data.model.MessageType
import com.orbit.app.data.model.User
import com.orbit.app.data.remote.RealtimeDatabaseSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val source: RealtimeDatabaseSource
) {
    fun chatId(uid1: String, uid2: String) = source.chatId(uid1, uid2)

    fun observeMessages(chatId: String): Flow<List<Message>> =
        source.observeMessages(chatId)

    suspend fun sendMessage(chatId: String, senderId: String, text: String) {
        val type = parseSlashCommand(text)
        source.sendMessage(
            chatId,
            Message(
                senderId = senderId,
                text = text,
                type = type,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun sendHeartMessage(chatId: String, senderId: String) {
        source.sendMessage(
            chatId,
            Message(
                senderId = senderId,
                text = "💗",
                type = MessageType.HEART,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    private fun parseSlashCommand(text: String): MessageType = when {
        text.startsWith("/miss")      -> MessageType.SLASH_MISS
        text.startsWith("/soon")      -> MessageType.SLASH_SOON
        text.startsWith("/goodnight") -> MessageType.SLASH_GOODNIGHT
        text.startsWith("/morning")   -> MessageType.SLASH_MORNING
        text.startsWith("/hug")       -> MessageType.SLASH_HUG
        else                          -> MessageType.TEXT
    }
}

@Singleton
class HeartRepository @Inject constructor(
    private val source: RealtimeDatabaseSource,
    private val authRepository: AuthRepository
) {
    suspend fun sendPulse(chatId: String) {
        val uid = authRepository.currentUser?.uid ?: return
        source.sendHeartPulse(
            chatId,
            HeartPulse(
                senderId = uid,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun observePulses(chatId: String): Flow<HeartPulse?> =
        source.observeHeartPulses(chatId)
}

@Singleton
class UserRepository @Inject constructor(
    private val source: RealtimeDatabaseSource
) {
    suspend fun getUser(uid: String): User? = source.getUser(uid)

    suspend fun getUserByPairCode(code: String): User? = source.getUserByPairCode(code)

    suspend fun pairUsers(myUid: String, partnerUid: String) =
        source.setPartner(myUid, partnerUid)
}
