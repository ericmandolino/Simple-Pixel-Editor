package com.swirlfist.simplepixel.domain.usecase

import android.content.Context
import android.net.Uri
import com.swirlfist.simplepixel.domain.error.SavePixelImageError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.FileOutputStream
import javax.inject.Inject

class SavePixelImageUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : SavePixelImageUseCase {
    override suspend fun invoke(params: SavePixelImageUseCase.Params): Result<Unit> {
        return try {
            savePixelImage(
                params.pixelImageModel,
                params.uri,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(SavePixelImageError(e))
        }
    }

    private fun savePixelImage(
        pixelImageModel: PixelImageModel,
        uri: Uri,
    ) {
        val contentResolver = applicationContext.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
            FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                outputStream.write(Json.encodeToString(pixelImageModel).toByteArray())
            }
        }
    }
}