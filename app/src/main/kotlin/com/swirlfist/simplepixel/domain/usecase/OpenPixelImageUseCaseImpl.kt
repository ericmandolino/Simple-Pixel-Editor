package com.swirlfist.simplepixel.domain.usecase

import android.content.Context
import android.net.Uri
import com.swirlfist.simplepixel.domain.error.OpenPixelImageError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class OpenPixelImageUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : OpenPixelImageUseCase {
    override suspend fun invoke(params: OpenPixelImageUseCase.Params): Result<PixelImageModel> {
        return try {
            Result.success(openPixelImage(params.uri))
        } catch(e: Exception) {
            Result.failure(OpenPixelImageError(e))
        }
    }

    private fun openPixelImage(
        uri: Uri,
    ) : PixelImageModel {
        val contentResolver = applicationContext.contentResolver

        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }

        return Json.decodeFromString<PixelImageModel>(stringBuilder.toString())
    }
}