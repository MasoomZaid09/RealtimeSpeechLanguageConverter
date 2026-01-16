import dotenv from "dotenv";

dotenv.config();

export async function generateChatResponse(prompt) {
  // Get the prompt prefix from environment variables
  const promptPrefix = process.env.GEMINI_PROMPT_PREFIX || "";
  
  // Concatenate the prefix with the user's message
  const finalPrompt = promptPrefix ? `${promptPrefix} ${prompt}` : prompt;

  console.log("FINAL PROMPT 👉", finalPrompt);

  const response = await fetch(
    `https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=${process.env.GEMINI_API_KEY}`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [
          { parts: [{ text: finalPrompt }] }
        ]
      })
    }
  );

  const data = await response.json();
  
  console.log("RAW GEMINI RESPONSE 👉", JSON.stringify(data, null, 2));

  return {
    reply: data
  };

}
