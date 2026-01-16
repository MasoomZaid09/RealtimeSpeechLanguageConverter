import { synthesizeSpeech } from "/Users/masoomzaid09/AndroidStudioProjects/SpeechLanguageConverterApp/SpeechLanguageConverter_Backend/src/services/text-to-speech.service.js";

/**
 * Controller for text-to-speech synthesis
 * POST /tts
 * Body: { text: string, languageCode?: string, voiceName?: string, audioEncoding?: string }
 * Returns: Audio file directly (no JSON, no saving)
 */
export async function textToSpeechController(req, res) {
  try {
    const { text, languageCode, voiceName, audioEncoding } = req.body;

    if (!text || typeof text !== "string" || text.trim().length === 0) {
      return res.status(400).json({ 
        error: "Text is required and must be a non-empty string" 
      });
    }

    // Optional: Limit text length to prevent abuse
    if (text.length > 5000) {
      return res.status(400).json({ 
        error: "Text length exceeds maximum limit of 5000 characters" 
      });
    }
    
    const options = {
      languageCode: languageCode || "en-IN",
      voiceName: voiceName || "en-IN-Neural2-D",
      audioEncoding: audioEncoding || "MP3",
      speakingRate: 0.88,
      pitch: -1.2,
    };

    const result = await synthesizeSpeech(text, options);

    // Set appropriate content type based on audio encoding
    const contentTypeMap = {
      MP3: "audio/mpeg",
      LINEAR16: "audio/pcm",
      OGG_OPUS: "audio/ogg",
      MULAW: "audio/basic",
      ALAW: "audio/basic",
    };

    const contentType = contentTypeMap[result.audioEncoding] || "audio/mpeg";

    // Send audio directly as binary response
    res.setHeader("Content-Type", contentType);
    res.setHeader("Content-Disposition", "inline; filename=audio.mp3");
    res.send(result.audioBuffer);
    console.log("Audio File Sent Successfully");
  } catch (error) {
    console.error("TTS Controller Error:", error);
    res.status(500).json({ 
      error: error.message || "Failed to synthesize speech" 
    });
  }
}
