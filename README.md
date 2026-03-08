# 🪐 Orbit — Setup Guide

A private, real-time chat app for two people. Just you and them.

---

## Before Opening in Android Studio

### 1. Create a Firebase Project
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create a new project called **Orbit**
3. Add an **Android app** with package name `com.orbit.app`
4. Download `google-services.json` and place it at:
   ```
   app/google-services.json
   ```

### 2. Enable Firebase Services
In the Firebase Console:
- **Authentication** → Sign-in method → enable **Email/Password**
- **Realtime Database** → Create database → start in **test mode** (lock down rules before release)
- ✅ No billing required — Realtime Database is fully covered by the free Spark plan

### 3. Realtime Database Security Rules (recommended)
```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null",
        ".write": "auth.uid === $uid"
      }
    },
    "chats": {
      "$chatId": {
        ".read": "auth != null && $chatId.contains(auth.uid)",
        ".write": "auth != null && $chatId.contains(auth.uid)"
      }
    },
    "heartbeats": {
      "$chatId": {
        ".read": "auth != null && $chatId.contains(auth.uid)",
        ".write": "auth != null && $chatId.contains(auth.uid)"
      }
    }
  }
}
```

---

## Open in Android Studio
1. Open Android Studio → **Open** → select the `orbit/` folder
2. Wait for Gradle sync to complete
3. Run on a device or emulator (API 26+)

---

## Features

| Feature | How it works |
|---|---|
| **Login / Register** | Firebase Auth (email + password) |
| **Pairing** | Share your 6-character code; enter partner's to connect |
| **Real-time chat** | Firestore `snapshotListener` — no polling |
| **Slash commands** | Type `/` to see menu: `/miss` `/soon` `/goodnight` `/morning` `/hug` |
| **Heart button** | Tap → haptic on your end + vibration on partner's device + heart animation |
| **Star background** | Animated twinkling stars + Pisces & Gemini constellations |
| **Phase animations** | Background adapts to morning / afternoon / evening / night |
| **Orbiting logo** | Two planets orbit each other on auth screens |

---

## Slash Commands Reference

| Command | Emoji | Description |
|---|---|---|
| `/miss` | 💛 | Missing you bubble |
| `/soon` | ⏳ | See you soon |
| `/goodnight` | 🌙 | Dark, soft goodnight |
| `/morning` | 🌅 | Warm sunrise greeting |
| `/hug` | 🫂 | Sending a hug |

---

## Package Structure
```
com.orbit.app/
├── di/              Hilt modules
├── data/
│   ├── model/       User, Message, HeartPulse
│   ├── remote/      FirestoreSource
│   └── repository/  AuthRepository, ChatRepository, HeartRepository, UserRepository
├── presentation/
│   ├── ui/theme/    Color, Type, Theme
│   ├── navigation/  NavGraph
│   ├── screens/
│   │   ├── auth/    LoginScreen, RegisterScreen + ViewModels
│   │   ├── home/    PairScreen + HomeViewModel
│   │   └── chat/    ChatScreen, MessageBubble + ChatViewModel
│   └── components/  StarBackground, OrbitLogo, HeartButton
└── utils/           PhaseCalculator, HapticUtil
```

---

## Notes
- `google-services.json` is **not included** — you must add your own
- Heartbeat pulses auto-delete after 10 seconds
- The app is designed for exactly 2 users — no group support
- Zodiac constellations drawn are Pisces (warm) + Gemini (cool blue)
