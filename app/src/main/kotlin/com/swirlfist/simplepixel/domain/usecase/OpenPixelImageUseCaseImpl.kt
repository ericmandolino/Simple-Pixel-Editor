package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.OpenPixelImageError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.presentation.mapper.toPixelImageModel
import com.swirlfist.simplepixel.presentation.model.PixelImageSaveModel
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OpenPixelImageUseCaseImpl @Inject constructor(
    private val readFromFileUseCase: ReadFromFileUseCase,
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : OpenPixelImageUseCase {
    override suspend fun invoke(params: OpenPixelImageUseCase.Params): Result<PixelImageModel> {
        return try {
            val pixelImage = openPixelImage(params.uri)
            pixelImageEditorActionRepository.clearActions()
            Result.success(pixelImage)
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