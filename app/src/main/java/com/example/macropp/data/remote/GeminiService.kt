package com.example.macropp.data.remote

import android.graphics.Bitmap
import com.example.macropp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeminiService @Inject constructor() {
    // Assuming you have your API key in local.properties as GEMINI_API_KEY
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun analyzeFoodImage(image: Bitmap): String {
        return withContext(Dispatchers.IO) {
            val prompt = """
                Analyze the food in this image and provide the estimated nutritional information.
                Return ONLY a JSON object with the following keys and integer values:
                "calories", "protein", "carbs", "fats".
                For example: {"calories": 500, "protein": 30, "carbs": 50, "fats": 20}
                If the image is not of food, return an empty JSON object: {}.
            """.trimIndent()

            val inputContent = content {
                image(image)
                text(prompt)
            }

            val response = generativeModel.generateContent(inputContent)
            var responseText = response.text ?: "{}"

            // CLEANUP: Remove potential markdown backticks that Gemini might add
            if (responseText.contains("```json")) {
                responseText = responseText.replace("```json", "").replace("```", "")
            } else if (responseText.contains("```")) {
                responseText = responseText.replace("```", "")
            }

            responseText.trim()
        }
    }
}