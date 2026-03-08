package com.orbit.app.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orbit.app.data.model.HeartPulse
import com.orbit.app.data.model.Message
import com.orbit.app.data.model.MessageType
import com.orbit.app.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// ── Flat maps for Realtime DB (no nested objects) ─────────

fun Message.toMap(): Map<String, Any?> = mapOf(
    "id"        to id,
    "senderId"  to senderId,
    "text"      to text,
    "type"      to type.name,
    "timestamp" to timestamp,
    "reaction"  to reaction
)

fun User.toMap(): Map<String, Any?> = mapOf(
    "uid"          to uid,
    "name"         to name,
    "email"        to email,
    "partnerId"    to partnerId,
    "pairCode"     to pairCode,
    "zodiac"       to zodiac,
    "avatarColor"  to avatarColor,
    "lastSeen"     to lastSeen
)

fun DataSnapshot.toMessage(): Message? = try {
    Message(
        id        = child("id").getValue(String::class.java) ?: key ?: "",
        senderId  = child("senderId").getValue(String::class.java) ?: "",
        text      = child("text").getValue(String::class.java) ?: "",
        type      = MessageType.valueOf(child("type").getValue(String::class.java) ?: "TEXT"),
        reaction  = child("reaction").getValue(String::class.java)
    )
} catch (e: Exception) { null }

fun DataSnapshot.toUser(): User? = try {
    User(
        uid         = child("uid").getValue(String::class.java) ?: "",
        name        = child("name").getValue(String::class.java) ?: "",
        email       = child("email").getValue(String::class.java) ?: "",
        partnerId   = child("partnerId").getValue(String::class.java) ?: "",
        pairCode    = child("pairCode").getValue(String::class.java) ?: "",
        zodiac      = child("zodiac").getValue(String::class.java) ?: "",
        avatarColor = child("avatarColor").getValue(String::class.java) ?: "#E8A598"
    )
} catch (e: Exception) { null }

@Singleton
class RealtimeDatabaseSource @Inject constructor(
    private val db: FirebaseDatabase
) {
    // ── Users ─────────────────────────────────────────────

    suspend fun createUser(user: User) {
        db.getReference("users/${user.uid}").setValue(user.toMap()).await()
    }

    suspend fun getUser(uid: String): User? {
        val snap = db.getReference("users/$uid").get().await()
        return if (snap.exists()) snap.toUser() else null
    }

    suspend fun getUserByPairCode(code: String): User? {
        val snap = db.getReference("users")
            .orderByChild("pairCode")
            .equalTo(code)
            .get().await()
        return snap.children.firstOrNull()?.toUser()
    }

    suspend fun setPartner(myUid: String, partnerUid: String) {
        db.getReference("users/$myUid/partnerId").setValue(partnerUid).await()
        db.getReference("users/$partnerUid/partnerId").setValue(myUid).await()
    }

    // ── Chat ──────────────────────────────────────────────

    fun chatId(uid1: String, uid2: String): String =
        listOf(uid1, uid2).sorted().joinToString("_")

    suspend fun sendMessage(chatId: String, message: Message) {
        val ref = db.getReference("chats/$chatId/messages").push()
        val withId = message.copy(id = ref.key ?: "")
        ref.setValue(withId.toMap()).await()
    }

    fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val ref = db.getReference("chats/$chatId/messages")
            .orderByChild("timestamp")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val msgs = snapshot.children.mapNotNull { it.toMessage() }
                trySend(msgs)
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ── Heart Pulses ──────────────────────────────────────

    suspend fun sendHeartPulse(chatId: String, pulse: HeartPulse) {
        val ref = db.getReference("heartbeats/$chatId/pulses").push()
        ref.setValue(mapOf(
            "id"        to (ref.key ?: ""),
            "senderId"  to pulse.senderId,
            "timestamp" to System.currentTimeMillis()
        )).await()

        // Auto-delete after 10 seconds
        kotlinx.coroutines.delay(10_000)
        ref.removeValue().await()
    }

    fun observeHeartPulses(chatId: String): Flow<HeartPulse?> = callbackFlow {
        val ref = db.getReference("heartbeats/$chatId/pulses")
            .orderByChild("timestamp")
            .limitToLast(1)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pulse = snapshot.children.lastOrNull()?.let {
                    HeartPulse(
                        id        = it.child("id").getValue(String::class.java) ?: "",
                        senderId  = it.child("senderId").getValue(String::class.java) ?: ""
                    )
                }
                trySend(pulse)
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
