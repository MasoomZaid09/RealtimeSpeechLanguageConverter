import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import chatRoutes from "/Users/masoomzaid09/AndroidStudioProjects/SpeechLanguageConverterApp/SpeechLanguageConverter_Backend/src/routes/chat.route.js";
import ttsRoutes from "/Users/masoomzaid09/AndroidStudioProjects/SpeechLanguageConverterApp/SpeechLanguageConverter_Backend/src/routes/text-to-speech.route.js";
import { createSpeechToTextServer } from "/Users/masoomzaid09/AndroidStudioProjects/SpeechLanguageConverterApp/SpeechLanguageConverter_Backend/src/services/speech-to-text.service.js";

dotenv.config();

// Express app setup
const app = express();
app.use(cors());
app.use(express.json({ limit: "50mb" })); // Increase limit for audio base64
app.use(express.raw({ type: "audio/*", limit: "50mb" })); // For raw audio uploads

app.use("/chat", chatRoutes);
app.use("/tts", ttsRoutes);

// Start Express server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Express server running on port ${PORT}`);
});

// Start Google Cloud WebSocket STT server
const STT_PORT = process.env.STT_PORT || 8080;
createSpeechToTextServer(STT_PORT);
