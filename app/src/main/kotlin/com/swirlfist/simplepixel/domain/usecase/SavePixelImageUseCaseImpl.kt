package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.domain.error.SavePixelImageError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.presentation.mapper.toPixelImageSaveModel
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SavePixelImageUseCaseImpl @Inject constructor(
    private val writeToFileUseCase: WriteToFileUseCase,
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

    private suspend fun savePixelImage(
        pixelImageModel: PixelImageModel,
        uri: Uri,
    ) {
        val content = Json.encodeToString(pixelImageModel.toPixelImageSaveModel())

        writeToFileUseCase.invoke(
            WriteToFileUseCase.Params(
                content,
                uri,
            )
        ).getOrThrow()
    }
}