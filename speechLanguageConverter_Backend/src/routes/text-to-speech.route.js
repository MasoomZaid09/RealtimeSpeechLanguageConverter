import { Router } from "express";
import { textToSpeechController } from "../controllers/text-to-speech.controller.js";

const router = Router();

// POST /tts - Convert text to speech (returns audio file directly)
router.post("/", textToSpeechController);

export default router;
