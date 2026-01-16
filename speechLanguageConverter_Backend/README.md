# AI Backend - Speech Language Converter

A Node.js backend service that provides AI-powered chat, text-to-speech, and speech-to-text capabilities using Google Cloud APIs and Google Gemini AI.

## Features

- 🤖 **AI Chat**: Interactive chat using Google Gemini AI (Gemini 2.0 Flash)
- 🔊 **Text-to-Speech**: Convert text to natural-sounding speech using Google Cloud Text-to-Speech API
- 🎤 **Speech-to-Text**: Real-time speech recognition using Google Cloud Speech-to-Text API via WebSocket

## Tech Stack

- **Runtime**: Node.js (ES Modules)
- **Framework**: Express.js
- **AI**: Google Gemini AI
- **Speech Services**: Google Cloud Speech-to-Text & Text-to-Speech
- **WebSocket**: ws library for real-time communication
- **Environment**: dotenv for configuration

## Prerequisites

Before you begin, ensure you have:

1. **Node.js** (v14 or higher)
2. **npm** or **yarn**
3. **Google Cloud Project** with the following APIs enabled:
   - Speech-to-Text API
   - Text-to-Speech API
4. **Google Cloud Service Account** with appropriate permissions
5. **Google Gemini API Key**

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd AI_Backend
```

2. Install dependencies:
```bash
npm install
```

3. Set up environment variables:
Create a `.env` file in the root directory:
```env
# Gemini API Configuration
GEMINI_API_KEY=your_gemini_api_key_here

# Server Configuration
PORT=3000
STT_PORT=8080

# Google Cloud Configuration
# Option 1: Path to service account JSON key file
GOOGLE_APPLICATION_CREDENTIALS=./credentials/google-cloud-key.json

# Option 2: Leave empty to use default credentials (gcloud auth)
# GOOGLE_APPLICATION_CREDENTIALS=
```

4. Set up Google Cloud credentials:

   **Option A: Using Service Account Key File (Recommended)**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Navigate to **IAM & Admin** → **Service Accounts**
   - Create a new service account or use an existing one
   - Grant the following roles:
     - **Cloud Speech-to-Text API User**
     - **Cloud Text-to-Speech API User**
   - Create a JSON key:
     - Click on the service account → **Keys** → **Add Key** → **Create new key** → **JSON**
     - Download the JSON file
   - Place the JSON file in `credentials/google-cloud-key.json`
   - Update `.env` with the path to your credentials file

   **Option B: Using Default Credentials (For Development)**
   ```bash
   # Install Google Cloud SDK
   brew install google-cloud-sdk  # macOS
   # Or download from: https://cloud.google.com/sdk/docs/install
   
   # Authenticate
   gcloud auth application-default login
   
   # Set your project
   gcloud config set project YOUR_PROJECT_ID
   ```

5. Enable required APIs:
```bash
gcloud services enable speech.googleapis.com
gcloud services enable texttospeech.googleapis.com
```

## Running the Server

Start the server:
```bash
npm start
```

You should see:
```
Express server running on port 3000
WebSocket STT server running on port 8080
```

## API Documentation

### 1. Chat API

**POST** `/chat`

Send a message to Google Gemini AI and receive a response.

#### Request
```json
{
  "message": "Hello, how are you?"
}
```

#### Response
```json
{
  "reply": {
    "candidates": [
      {
        "content": {
          "parts": [
            {
              "text": "I'm doing well, thank you for asking!"
            }
          ]
        }
      }
    ]
  }
}
```

#### Example
```bash
curl -X POST http://localhost:3000/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, how are you?"}'
```

---

### 2. Text-to-Speech API

**POST** `/tts`

Convert text to speech audio. Returns audio file directly as binary response.

#### Request
```json
{
  "text": "Hello, this is a test message",
  "languageCode": "en-IN",        // Optional, default: "en-IN"
  "voiceName": "en-IN-Neural2-D", // Optional, default: "en-IN-Neural2-D"
  "audioEncoding": "MP3"          // Optional, default: "MP3"
}
```

#### Response
- **Content-Type**: `audio/mpeg` (for MP3)
- **Body**: Binary audio data

#### Supported Audio Encodings
- `MP3` - MP3 format (default)
- `LINEAR16` - 16-bit linear PCM
- `OGG_OPUS` - Ogg Opus format
- `MULAW` - μ-law format
- `ALAW` - A-law format

#### Example
```bash
curl -X POST http://localhost:3000/tts \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello, this is a test message", "audioEncoding": "MP3"}' \
  --output output.mp3
```

#### JavaScript Example
```javascript
const response = await fetch('http://localhost:3000/tts', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    text: 'Hello, how are you?',
    languageCode: 'en-IN',
    audioEncoding: 'MP3'
  })
});

const audioBlob = await response.blob();
const audioUrl = URL.createObjectURL(audioBlob);
const audio = new Audio(audioUrl);
audio.play();
```

---

### 3. Speech-to-Text API (WebSocket)

**WebSocket** `ws://localhost:8080`

Real-time speech-to-text conversion using WebSocket streaming.

#### Connection
```javascript
const ws = new WebSocket('ws://localhost:8080');
```

#### Client → Server
Send audio chunks as JSON:
```json
{
  "audioChunk": "base64_encoded_audio_data",
  "isLast": false
}
```

- `audioChunk`: Base64-encoded audio data (LINEAR16, 16kHz, Mono)
- `isLast`: `true` when sending the last chunk

#### Server → Client
Receive transcriptions:
```json
{
  "transcript": "Hello world",
  "isFinal": false
}
```

- `transcript`: The transcribed text
- `isFinal`: `true` for final results, `false` for interim results

#### Error Response
```json
{
  "error": "Error message"
}
```

#### Audio Format Requirements
- **Encoding**: LINEAR16 (PCM)
- **Sample Rate**: 16000 Hz
- **Channels**: Mono
- **Language**: en-IN (configurable in service)

#### JavaScript Example
```javascript
const ws = new WebSocket('ws://localhost:8080');

ws.onopen = () => {
  console.log('Connected to STT server');
  
  // Send audio chunk
  const audioChunk = base64AudioData; // Your base64 audio data
  ws.send(JSON.stringify({
    audioChunk: audioChunk,
    isLast: false
  }));
};

ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  if (data.transcript) {
    console.log('Transcript:', data.transcript, 'Final:', data.isFinal);
  }
  if (data.error) {
    console.error('Error:', data.error);
  }
};

ws.onerror = (error) => {
  console.error('WebSocket error:', error);
};
```

---

## Project Structure

```
AI_Backend/
├── server.js                    # Main server entry point
├── src/
│   ├── app.js                   # Express app configuration
│   ├── controllers/
│   │   ├── chat.controller.js           # Chat controller
│   │   ├── text-to-speech.controller.js # TTS controller
│   │   └── ...
│   ├── routes/
│   │   ├── chat.route.js                # Chat routes
│   │   ├── text-to-speech.route.js      # TTS routes
│   │   └── ...
│   └── services/
│       ├── gemini.services.js           # Gemini AI service
│       ├── text-to-speech.service.js    # Google Cloud TTS service
│       ├── speech-to-text.service.js    # Google Cloud STT service
│       └── ...
├── package.json
├── .env                          # Environment variables (not in repo)
└── README.md
```

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `GEMINI_API_KEY` | Google Gemini API key | Yes |
| `PORT` | Express server port | No (default: 3000) |
| `STT_PORT` | WebSocket STT server port | No (default: 8080) |
| `GOOGLE_APPLICATION_CREDENTIALS` | Path to Google Cloud service account JSON | Yes* |

*Can be omitted if using `gcloud auth application-default login`

## Troubleshooting

### Error: "Could not load the default credentials"
**Solution**: 
- Make sure `GOOGLE_APPLICATION_CREDENTIALS` points to a valid JSON file, OR
- Run `gcloud auth application-default login`

### Error: "Speech-to-Text API has not been used"
**Solution**: Enable the Speech-to-Text API in Google Cloud Console

### Error: "Permission denied"
**Solution**: Make sure your service account has the required roles:
- **Cloud Speech-to-Text API User**
- **Cloud Text-to-Speech API User**

### WebSocket connection refused
**Solution**: 
- Check if port 8080 is available
- Check firewall settings
- Verify `STT_PORT` in `.env`

### Gemini API errors
**Solution**: 
- Verify your `GEMINI_API_KEY` is correct
- Check your API quota and billing status
- Ensure the API key has proper permissions

## Cost Considerations

### Google Cloud Speech-to-Text
- First 60 minutes per month: **Free**
- After that: ~$0.006 per 15 seconds

### Google Cloud Text-to-Speech
- First 0-4 million characters per month: **Free**
- 4-100 million characters: $4.00 per 1 million characters
- 100+ million characters: $2.00 per 1 million characters

### Google Gemini AI
- Check current pricing at [Google AI Studio](https://makersuite.google.com/app/apikey)

Monitor usage in [Google Cloud Console](https://console.cloud.google.com/billing)

## Production Considerations

1. **Security**: 
   - Use HTTPS/WSS in production
   - Add authentication to API endpoints and WebSocket connections
   - Implement rate limiting

2. **Error Handling**: 
   - Add comprehensive error handling and logging
   - Implement retry mechanisms for API calls

3. **Monitoring**: 
   - Set up monitoring for API usage and costs
   - Add health check endpoints
   - Implement request logging

4. **Performance**: 
   - Consider caching for frequently used voices
   - Implement connection pooling
   - Add request validation middleware

## License

ISC

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and questions, please open an issue on GitHub.
