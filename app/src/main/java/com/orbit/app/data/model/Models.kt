package com.orbit.app.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val partnerId: String = "",
    val pairCode: String = "",
    val zodiac: String = "",
    val avatarColor: String = "#E8A598",
    val lastSeen: Long = System.currentTimeMillis()
)

enum class MessageType {
    TEXT, SLASH_MISS, SLASH_SOON, SLASH_GOODNIGHT, SLASH_MORNING, SLASH_HUG, HEART
}

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val reaction: String? = null
)

data class HeartPulse(
    val id: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
