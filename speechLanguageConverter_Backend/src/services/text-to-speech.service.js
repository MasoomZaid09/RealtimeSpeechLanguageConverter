import { TextToSpeechClient } from "@google-cloud/text-to-speech";
import { existsSync } from "node:fs";
import dotenv from "dotenv";

dotenv.config();

// Initialize Google Cloud Text-to-Speech client
let client;

try {
  // Check if credentials path is set and valid
  const credsPath = process.env.GOOGLE_APPLICATION_CREDENTIALS;
  
  if (credsPath && credsPath !== "path/to/your/service-account-key.json" && existsSync(credsPath)) {
    // Use service account key file
    client = new TextToSpeechClient({
      keyFilename: credsPath,
    });
    console.log(`TTS: Using Google Cloud credentials from: ${credsPath}`);
  } else if (credsPath && credsPath !== "path/to/your/service-account-key.json" && !existsSync(credsPath)) {
    console.warn(`TTS: Warning: Credentials file not found at ${credsPath}. Trying default credentials...`);
    client = new TextToSpeechClient();
  } else {
    // Use default credentials (gcloud auth application-default login)
    client = new TextToSpeechClient();
    console.log("TTS: Using Google Cloud default credentials (gcloud auth)");
  }
} catch (error) {
  console.error("TTS: Error initializing Text-to-Speech Client:", error.message);
  console.error("Please set up Google Cloud credentials. See SPEECH_TO_TEXT_SETUP.md for details.");
  throw error;
}

/**
 * Converts text to speech audio
 * @param {string} text - The text to convert to speech
 * @param {Object} options - Optional configuration
 * @param {string} options.languageCode - Language code (default: 'en-IN')
 * @param {string} options.voiceName - Voice name (default: 'en-IN-Wavenet-A')
 * @param {string} options.ssmlGender - Gender (default: 'NEUTRAL')
 * @param {string} options.audioEncoding - Audio encoding (default: 'MP3')
 * @returns {Promise<Object>} - Audio buffer and metadata
 */
export async function synthesizeSpeech(text, options = {}) {
  try {
    const {
      languageCode = "en-IN",
      voiceName = "en-IN-Wavenet-A",
      ssmlGender = "NEUTRAL",
      audioEncoding = "MP3",
    } = options;

    const request = {
      input: { text: text },
      voice: {
        languageCode: languageCode,
        name: voiceName,
        ssmlGender: ssmlGender,
      },
      audioConfig: {
        audioEncoding: audioEncoding,
      },
    };

    const [response] = await client.synthesizeSpeech(request);
    
    // Return raw audio buffer
    const audioContent = response.audioContent;

    return {
      audioBuffer: audioContent,
      audioEncoding: audioEncoding,
      languageCode: languageCode,
      voiceName: voiceName,
    };
  } catch (error) {
    console.error("TTS Error:", error);
    throw new Error(`Text-to-Speech synthesis failed: ${error.message}`);
  }
}
