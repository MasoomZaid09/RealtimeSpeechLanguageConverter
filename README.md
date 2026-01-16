# Realtime Speech Language Converter

A real-time speech translation system that captures spoken input on an Android device, translates it into English based on the user's context, and returns the translated output as speech. The system supports both technical and non-technical users.

---

## Problem Statement

Many users with technical knowledge struggle to explain ideas clearly in English. This project solves that by fine-tuning a model to respond based on the user's spoken input:  
- Technical speech → returns formal technical English  
- Non-technical speech → returns simple, natural English  

---

## Key Features

- Real-time speech capture on Android  
- Backend translation processing  
- Voice output in translated English  
- WebSocket support for low-latency transcription  
- Environment-based configuration for secure API usage  

---

## Technology Stack

- **Android:** Kotlin, WebSocket  
- **Backend:** Node.js, Express.js  
- **Communication:** REST API + WebSocket  

---

## Folder Structure

RealtimeSpeechLanguageConverter/
│
├── speechLanguageConverter_AndroidApp/
│   ├── app/
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/com/yourpackage/
│   │   │       │   ├── ui/          # Screens / UI components
│   │   │       │   ├── viewmodel/   # State & business logic
│   │   │       │   ├── data/        # Repositories & data sources
│   │   │       │   ├── audio/       # Audio capture & playback
│   │   │       │   └── network/     # API / WebSocket communication
│   │   │       ├── res/             # Layouts, strings, themes
│   │   │       └── AndroidManifest.xml
│   │   └── build.gradle
│   └── gradle/
│
├── speechLanguageConverter_Backend/
│   ├── src/
│   │   ├── routes/          # API routing
│   │   ├── controllers/     # Request handling
│   │   ├── services/        # Translation / speech logic
│   │   └── utils/           # Shared utilities
│   ├── package.json
│   └── server.js
│
└── README.md

---

## Setup & Run

### Clone the repository

```bash
git clone https://github.com/MasoomZaid09/RealtimeSpeechLanguageConverter.git
cd RealtimeSpeechLanguageConverter
