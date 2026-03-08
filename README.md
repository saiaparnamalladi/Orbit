<div align="center">

# 🪐 Orbit

**A private space for two.**

Real-time chat app for couples — built with Kotlin, Jetpack Compose, and Firebase.

![Android](https://img.shields.io/badge/Android-API%2026%2B-brightgreen?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-7F52FF?style=flat-square&logo=kotlin)
![Firebase](https://img.shields.io/badge/Firebase-Realtime%20DB-FFCA28?style=flat-square&logo=firebase)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.13-4285F4?style=flat-square&logo=jetpackcompose)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)

</div>

---

## ✨ Features

- 💬 **Real-time chat** — messages appear instantly via Firebase Realtime Database listeners
- 💗 **Heart button** — tap to send a haptic pulse to your partner's device
- 🌙 **Slash commands** — type `/` to access expressive message types
- 🌅 **Phase-aware UI** — background adapts to morning, afternoon, evening, and night
- ⭐ **Star field** — animated twinkling stars with Pisces & Gemini constellations drawn in Canvas
- 🪐 **Orbiting logo** — two planets orbit each other on the auth screens
- 🔐 **Auth** — email/password login via Firebase Auth
- 🔗 **Pairing** — connect with your partner using a private 6-character code

---

## 🌙 Slash Commands

Type `/` in the chat input to open the command menu:

| Command | Effect |
|---|---|
| `/miss` | 💛 sends a warm "missing you" bubble |
| `/soon` | ⏳ "see you soon" message |
| `/goodnight` | 🌙 soft dark goodnight bubble |
| `/morning` | 🌅 warm sunrise greeting |
| `/hug` | 🫂 sending a hug |

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.23 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Backend | Firebase Realtime Database |
| Auth | Firebase Authentication |
| Navigation | Compose Navigation |
| Async | Kotlin Coroutines + Flow |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11+
- A Firebase account (free Spark plan is enough)

### 1. Clone the repo
```bash
git clone https://github.com/yourusername/orbit.git
cd orbit
```

### 2. Set up Firebase
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create a new project
3. Add an Android app with package name `com.orbit.app`
4. Download `google-services.json` and place it at `app/google-services.json`
5. Enable **Authentication** → Email/Password
6. Enable **Realtime Database** → start in test mode

### 3. Set database rules
In the Firebase Console → Realtime Database → Rules:
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

### 4. Build & run
Open the project in Android Studio, wait for Gradle sync, and run on a device or emulator (API 26+).

---

## 📁 Project Structure

```
com.orbit.app/
├── di/                     Hilt dependency injection
├── data/
│   ├── model/              User, Message, HeartPulse
│   ├── remote/             RealtimeDatabaseSource
│   └── repository/         AuthRepository, ChatRepository,
│                           HeartRepository, UserRepository
├── presentation/
│   ├── ui/theme/           Color, Type, Theme (rose gold + deep space)
│   ├── navigation/         NavGraph with splash screen
│   ├── screens/
│   │   ├── auth/           LoginScreen, RegisterScreen
│   │   ├── home/           PairScreen (6-digit pairing)
│   │   └── chat/           ChatScreen, MessageBubble
│   └── components/         StarBackground, OrbitLogo, HeartButton
└── utils/                  PhaseCalculator, HapticUtil
```

---

## 📱 Navigation Flow

```
App launch
    │
    ├── not logged in ──→ Login ──→ Register
    │
    └── logged in
            │
            ├── no partner ──→ Pair Screen (share/enter 6-digit code)
            │
            └── paired ──→ Chat
```

---

## 🔒 Privacy

Orbit is designed for exactly **two people**. There are no public channels, no discovery features, and no third parties. Messages are only readable by the two paired users, enforced by Firebase security rules.

---

## 📝 Notes

- `google-services.json` is not included — you must add your own
- Heart pulses auto-delete from the database after 10 seconds
- The Pisces constellation is rendered in warm rose tones; Gemini in cool blue
- Phase detection uses the device's local time
- MVVM + Clean Architecture with unidirectional data flow.

---

<div align="center">

made with 💛 for someone far away

</div>
