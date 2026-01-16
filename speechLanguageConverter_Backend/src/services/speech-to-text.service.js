import { WebSocketServer } from "ws";
import { SpeechClient } from "@google-cloud/speech";
import dotenv from "dotenv";
import { existsSync } from "node:fs";

dotenv.config();

// Initialize Google Cloud Speech client
// Will use GOOGLE_APPLICATION_CREDENTIALS from .env or default credentials
let client;

try {
  // Check if credentials path is set and valid
  const credsPath = process.env.GOOGLE_APPLICATION_CREDENTIALS;
  
  if (credsPath && credsPath !== "path/to/your/service-account-key.json" && existsSync(credsPath)) {
    // Use service account key file
    client = new SpeechClient({
      keyFilename: credsPath,
    });
    console.log(`Using Google Cloud credentials from: ${credsPath}`);
  } else if (credsPath && credsPath !== "path/to/your/service-account-key.json" && !existsSync(credsPath)) {
    console.warn(`Warning: Credentials file not found at ${credsPath}. Trying default credentials...`);
    client = new SpeechClient();
  } else {
    // Use default credentials (gcloud auth application-default login)
    client = new SpeechClient();
    console.log("Using Google Cloud default credentials (gcloud auth)");
  }
} catch (error) {
  console.error("Error initializing Speech Client:", error.message);
  console.error("Please set up Google Cloud credentials. See SPEECH_TO_TEXT_SETUP.md for details.");
  throw error;
}

/**
 * Creates and starts a WebSocket server for Speech-to-Text
 * @param {number} port - Port number for WebSocket server
 * @returns {WebSocketServer} - The WebSocket server instance
 */
export function createSpeechToTextServer(port = 8080) {
  const wss = new WebSocketServer({ port });

  console.log(`WebSocket STT server running on port ${port}`);

  wss.on("connection", (ws) => {
    console.log("Client connected to STT");

    let recognizeStream = null;
    let streamActive = false;

    // Function to start STT stream
    const startStream = () => {
      console.log("Starting STT stream...");
      recognizeStream = client
        .streamingRecognize({
          config: {
            encoding: "LINEAR16",
            sampleRateHertz: 16000,
            languageCode: "en-IN",
          },
          interimResults: true,
        })
        .on("data", (data) => {
          const result = data.results[0];
          if (!result) return;

          const text = result.alternatives[0]?.transcript || "";
          const isFinal = result.isFinal || false;

          if (text) {
            ws.send(JSON.stringify({ transcript: text, isFinal }));
          }
        })
        .on("error", (err) => {
          console.error("STT Error:", err);
          ws.send(JSON.stringify({ error: err.message }));
          cleanupStream();
        });

      streamActive = true;
    };

    // Cleanup function
    const cleanupStream = () => {
      if (recognizeStream) {
        recognizeStream.end();
        recognizeStream = null;
      }
      streamActive = false;
    };

    // Handle incoming audio from client
    ws.on("message", (message) => {
      try {
        const { audioChunk, isLast } = JSON.parse(message);

        // Start stream only when first audio chunk arrives
        if (!streamActive && audioChunk) {
          startStream();
        }

        if (audioChunk && recognizeStream) {
          recognizeStream.write(Buffer.from(audioChunk, "base64"));
        }

        if (isLast) {
          cleanupStream();
        }
      } catch (e) {
        console.error("Message error:", e);
        ws.send(JSON.stringify({ error: "Invalid message format" }));
      }
    });

    // Handle client disconnect
    ws.on("close", () => {
      console.log("Client disconnected from STT");
      cleanupStream();
    });
    
    // Handle connection errors
    ws.on("error", (error) => {
      console.error("WebSocket error:", error);
      cleanupStream();
    });
  });

  return wss;
}
