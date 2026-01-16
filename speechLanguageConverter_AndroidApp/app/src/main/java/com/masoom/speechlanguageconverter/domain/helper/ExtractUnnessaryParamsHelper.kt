package com.masoom.speechlanguageconverter.domain.helper

import com.masoom.speechlanguageconverter.domain.model.TranslateResponse

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

