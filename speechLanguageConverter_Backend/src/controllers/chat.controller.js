import { generateChatResponse } from "../services/gemini.services.js";

export async function chatController(req, res) {
  try {
    const { message } = req.body;

    const result = await generateChatResponse(message);
    res.json(result);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
}
