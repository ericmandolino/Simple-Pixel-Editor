package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.domain.error.OpenPixelImageError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.presentation.mapper.toPixelImageModel
import com.swirlfist.simplepixel.presentation.model.PixelImageSaveModel
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OpenPixelImageUseCaseImpl @Inject constructor(
    private val readFromFileUseCase: ReadFromFileUseCase,
) : OpenPixelImageUseCase {
    override suspend fun invoke(params: OpenPixelImageUseCase.Params): Result<PixelImageModel> {
        return try {
            Result.success(openPixelImage(params.uri))
        } catch (e: Exception) {
            Result.failure(OpenPixelImageError(e))
        }
    }

    private suspend fun openPixelImage(
        uri: Uri,
    ): PixelImageModel {
        return readFromFileUseCase.invoke(
            params = ReadFromFileUseCase.Params(
                uri,
            )
        ).getOrThrow().let { content ->
            Json.decodeFromString<PixelImageSaveModel>(content).toPixelImageModel()
        }
    }
}