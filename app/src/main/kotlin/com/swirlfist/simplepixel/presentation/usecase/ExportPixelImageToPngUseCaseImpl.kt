package com.swirlfist.simplepixel.presentation.usecase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toArgb
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.usecase.ExportPixelImageToPngUseCase
import com.swirlfist.simplepixel.presentation.getPixelHeight
import com.swirlfist.simplepixel.presentation.getPixelWidth
import javax.inject.Inject
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.swirlfist.simplepixel.domain.error.ExportPixelImageError
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileOutputStream

class ExportPixelImageToPngUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : ExportPixelImageToPngUseCase {
    override suspend fun invoke(params: ExportPixelImageToPngUseCase.Params): Result<Unit> {
        return try {
            exportPixelImageToPng(
                params.pixelImageModel,
                params.uri,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ExportPixelImageError(e))
        }
    }

    private fun exportPixelImageToPng(
        pixelImageModel: PixelImageModel,
        uri: Uri,
    ) {
        val width = pixelImageModel.getPixelWidth()
        val height = pixelImageModel.getPixelHeight()

        val bitmap = createBitmap(width, height)
        val pixelMatrix = pixelImageModel.pixelMatrixModel.content
        val paletteColors = pixelImageModel.paletteModel.colors.map { colorLong ->
            Color.fromColorLong(colorLong).toArgb()
        }

        pixelMatrix.forEachIndexed { y, row ->
            row.forEachIndexed { x, pixel ->
                val paletteIndex = pixel.paletteIndex
                if (paletteIndex in paletteColors.indices) {
                    bitmap[x, bitmap.height - 1 - y] = paletteColors[paletteIndex]
                }
            }
        }

        val contentResolver = applicationContext.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
            FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream,
                )
            }
        }
    }
}