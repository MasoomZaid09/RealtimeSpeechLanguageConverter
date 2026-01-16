# Text-to-Speech API Documentation

## Overview
This API converts text to speech audio using Google Cloud Text-to-Speech API.

## Endpoints

### 1. Convert Text to Speech
**POST** `/tts`

Converts text to audio and returns it as base64-encoded audio data.

#### Request Body
```json
{
  "text": "Hello, this is a test message",
  "languageCode": "en-IN",        // Optional, default: "en-IN"
  "voiceName": "en-IN-Wavenet-A", // Optional, default: "en-IN-Wavenet-A"
  "audioEncoding": "MP3"          // Optional, default: "MP3"
}
```

#### Response
```json
{
  "success": true,
  "audioContent": "base64_encoded_audio_data",
  "audioEncoding": "MP3",
  "languageCode": "en-IN",
  "voiceName": "en-IN-Wavenet-A"
}
```

#### Example using curl
```bash
curl -X POST http://localhost:3000/tts \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Hello, this is a test message",
    "languageCode": "en-IN",
    "audioEncoding": "MP3"
  }'
```

### 2. Get Available Voices
**GET** `/tts/voices?languageCode=en-IN`

Returns a list of available voices for a given language.

#### Query Parameters
- `languageCode` (optional): Language code (default: "en-IN")

#### Response
```json
{
  "success": true,
  "languageCode": "en-IN",
  "voices": [
    {
      "name": "en-IN-Wavenet-A",
      "ssmlGender": "FEMALE",
      "naturalSampleRateHertz": 24000
    },
    {
      "name": "en-IN-Wavenet-B",
      "ssmlGender": "MALE",
      "naturalSampleRateHertz": 24000
    }
  ]
}
```

#### Example using curl
```bash
curl http://localhost:3000/tts/voices?languageCode=en-IN
```

## Audio Encoding Options

- `MP3` - MP3 format (default)
- `LINEAR16` - 16-bit linear PCM
- `OGG_OPUS` - Ogg Opus format
- `MULAW` - μ-law format
- `ALAW` - A-law format

## Supported Languages

Common language codes:
- `en-IN` - English (India)
- `en-US` - English (United States)
- `en-GB` - English (United Kingdom)
- `hi-IN` - Hindi (India)
- `es-ES` - Spanish (Spain)
- `fr-FR` - French (France)
- `de-DE` - German (Germany)

## Voice Names (en-IN)

- `en-IN-Wavenet-A` - Female voice (default)
- `en-IN-Wavenet-B` - Male voice
- `en-IN-Wavenet-C` - Female voice
- `en-IN-Wavenet-D` - Male voice
- `en-IN-Standard-A` - Female voice
- `en-IN-Standard-B` - Male voice

## Usage Examples

### JavaScript/Node.js
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

const data = await response.json();
const audioBase64 = data.audioContent;

// Convert base64 to audio blob
const audioBlob = Uint8Array.from(atob(audioBase64), c => c.charCodeAt(0));
```

### Android/Kotlin
```kotlin
// Using Retrofit
interface TtsApi {
    @POST("tts")
    suspend fun textToSpeech(@Body request: TtsRequest): TtsResponse
}

data class TtsRequest(
    val text: String,
    val languageCode: String = "en-IN",
    val voiceName: String = "en-IN-Wavenet-A",
    val audioEncoding: String = "MP3"
)

data class TtsResponse(
    val success: Boolean,
    val audioContent: String,
    val audioEncoding: String,
    val languageCode: String,
    val voiceName: String
)

// Usage
val request = TtsRequest(text = "Hello, this is a test")
val response = apiService.textToSpeech(request)

// Decode base64 to audio bytes
val audioBytes = Base64.decode(response.audioContent, Base64.DEFAULT)

// Play audio
val audioUri = saveAudioToFile(audioBytes) // Your function to save
mediaPlayer.setDataSource(context, audioUri)
mediaPlayer.prepare()
mediaPlayer.start()
```

### Python
```python
import requests
import base64

response = requests.post('http://localhost:3000/tts', json={
    'text': 'Hello, this is a test',
    'languageCode': 'en-IN',
    'audioEncoding': 'MP3'
})

data = response.json()
audio_base64 = data['audioContent']
audio_bytes = base64.b64decode(audio_base64)

# Save to file
with open('output.mp3', 'wb') as f:
    f.write(audio_bytes)
```

## Error Responses

### 400 Bad Request
```json
{
  "error": "Text is required and must be a non-empty string"
}
```

### 500 Internal Server Error
```json
{
  "error": "Text-to-Speech synthesis failed: [error message]"
}
```

## Rate Limits & Costs

Google Cloud Text-to-Speech pricing:
- First 0-4 million characters per month: Free
- 4-100 million characters: $4.00 per 1 million characters
- 100+ million characters: $2.00 per 1 million characters

Monitor usage in [Google Cloud Console](https://console.cloud.google.com/billing)

## Notes

- Maximum text length: 5000 characters per request
- Audio is returned as base64-encoded string
- The same Google Cloud credentials used for Speech-to-Text will work for Text-to-Speech
- Make sure Text-to-Speech API is enabled in your Google Cloud project
