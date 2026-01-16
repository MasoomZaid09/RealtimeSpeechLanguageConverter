package com.masoom.speechlanguageconverter.domain.helper

import com.masoom.speechlanguageconverter.data.model.TranslateResponse

fun extractTranslatedText(
    response: TranslateResponse
): String? {
    return response.reply
        ?.candidates
        ?.firstOrNull()
        ?.content
        ?.parts
        ?.firstOrNull()
        ?.text
}

