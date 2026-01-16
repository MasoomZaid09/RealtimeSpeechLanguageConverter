# Speech-to-Text Setup Guide

## Overview
This backend now includes WebSocket-based Speech-to-Text functionality using Google Cloud Speech-to-Text API.

## Prerequisites

1. **Google Cloud Project** with Speech-to-Text API enabled
2. **Service Account** with Speech-to-Text permissions
3. **Service Account Key** (JSON file)

## Setup Steps

### 1. Install Dependencies

```bash
npm install
```

This will install:
- `ws` - WebSocket library
- `@google-cloud/speech` - Google Cloud Speech-to-Text client

### 2. Google Cloud Setup

#### Option A: Using Service Account Key File (Recommended for Production)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Navigate to **IAM & Admin** → **Service Accounts**
3. Create a new service account or use an existing one
4. Grant the **Cloud Speech-to-Text API User** role
5. Create a JSON key:
   - Click on the service account → **Keys** → **Add Key** → **Create new key** → **JSON**
   - Download the JSON file
6. Place the JSON file in your project (e.g., `credentials/google-cloud-key.json`)
7. Update `.env`:
   ```env
   GOOGLE_APPLICATION_CREDENTIALS=./credentials/google-cloud-key.json
   ```

#### Option B: Using Default Credentials (For Development)

1. Install Google Cloud SDK:
   ```bash
   # macOS
   brew install google-cloud-sdk
   
   # Or download from: https://cloud.google.com/sdk/docs/install
   ```

2. Authenticate:
   ```bash
   gcloud auth application-default login
   ```

3. Set your project:
   ```bash
   gcloud config set project YOUR_PROJECT_ID
   ```

4. Leave `GOOGLE_APPLICATION_CREDENTIALS` empty in `.env` or don't set it

### 3. Enable Speech-to-Text API

```bash
gcloud services enable speech.googleapis.com
```

Or enable it from the [Google Cloud Console](https://console.cloud.google.com/apis/library/speech.googleapis.com)

### 4. Configure Environment Variables

Update your `.env` file:

```env
# Gemini API Configuration
GEMINI_API_KEY=your_gemini_api_key

# Server Configuration
PORT=3000

# WebSocket STT Server Port
STT_PORT=8080

# Google Cloud Speech-to-Text Configuration
# Option 1: Path to service account JSON key file
GOOGLE_APPLICATION_CREDENTIALS=./credentials/google-cloud-key.json

# Option 2: Leave empty to use default credentials (gcloud auth)
# GOOGLE_APPLICATION_CREDENTIALS=
```

### 5. Start the Server

```bash
npm start
```

You should see:
```
Express server running on port 3000
WebSocket STT server running on port 8080
```

## API Endpoints

### HTTP Endpoints
- **POST** `/chat` - Chat with Gemini AI

### WebSocket Endpoints
- **WS** `ws://localhost:8080` - Speech-to-Text streaming

## WebSocket Protocol

### Client → Server

Send audio chunks as JSON:

```json
{
  "audioChunk": "base64_encoded_audio_data",
  "isLast": false
}
```

- `audioChunk`: Base64-encoded audio data (LINEAR16, 16kHz)
- `isLast`: `true` when sending the last chunk

### Server → Client

Receive transcriptions:

```json
{
  "transcript": "Hello world",
  "isFinal": false
}
```

- `transcript`: The transcribed text
- `isFinal`: `true` for final results, `false` for interim results

### Error Response

```json
{
  "error": "Error message"
}
```

## Audio Format Requirements

- **Encoding**: LINEAR16 (PCM)
- **Sample Rate**: 16000 Hz
- **Channels**: Mono
- **Language**: en-IN (configurable in `speech-to-text.service.js`)

## Testing

### Using wscat (WebSocket client)

```bash
npm install -g wscat
wscat -c ws://localhost:8080
```

### Using curl (for HTTP endpoints)

```bash
curl -X POST http://localhost:3000/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## Troubleshooting

### Error: "Could not load the default credentials"

**Solution**: 
- Make sure `GOOGLE_APPLICATION_CREDENTIALS` points to a valid JSON file, OR
- Run `gcloud auth application-default login`

### Error: "Speech-to-Text API has not been used"

**Solution**: Enable the Speech-to-Text API in Google Cloud Console

### Error: "Permission denied"

**Solution**: Make sure your service account has the **Cloud Speech-to-Text API User** role

### WebSocket connection refused

**Solution**: 
- Check if port 8080 is available
- Check firewall settings
- Verify `STT_PORT` in `.env`

## Production Considerations

1. **Security**: Use HTTPS/WSS in production
2. **Authentication**: Add authentication to WebSocket connections
3. **Rate Limiting**: Implement rate limiting for WebSocket connections
4. **Error Handling**: Add more robust error handling and logging
5. **Monitoring**: Set up monitoring for API usage and costs

## Cost Notes

Google Cloud Speech-to-Text pricing:
- First 60 minutes per month: Free
- After that: ~$0.006 per 15 seconds

Monitor usage in [Google Cloud Console](https://console.cloud.google.com/billing)
