package com.swirlfist.simplepixel.domain.usecase

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import com.swirlfist.simplepixel.domain.error.ExportPixelImageError
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.presentation.height
import com.swirlfist.simplepixel.presentation.toHexCode
import com.swirlfist.simplepixel.presentation.width
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileOutputStream
import javax.inject.Inject

class ExportPixelImageUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : ExportPixelImageUseCase {
    override suspend fun invoke(params: ExportPixelImageUseCase.Params): Result<Unit> {
        return try {
            exportPixelImage(
                params.pixelImageModel,
                params.uri,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ExportPixelImageError(e))
        }
    }

    private fun exportPixelImage(
        pixelImageModel: PixelImageModel,
        uri: Uri,
    ) {
        val contentResolver = applicationContext.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
            FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                val pixelMatrix = pixelImageModel.pixelMatrixModel
                writeHeader(
                    out = outputStream,
                    width = pixelMatrix.width(),
                    height = pixelMatrix.height(),
                )
                writeContent(
                    out = outputStream,
                    pixelMatrix,
                    palette = pixelImageModel.paletteModel,
                )
                writeFooter(outputStream)
            }
        }
    }

    private fun writeHeader(
        out: FileOutputStream,
        width: Int,
        height: Int,
    ) {
        val header = """<svg width="$width" height="$height" xmlns="http://www.w3.org/2000/svg">
            |
        """.trimMargin()
        out.write(header.toByteArray())
    }

    private fun writeFooter(
        out: FileOutputStream,
    ) {
        out.write("</svg>".toByteArray())
    }

    private fun writeContent(
        out: FileOutputStream,
        pixelMatrix: PixelMatrixModel,
        palette: PaletteModel,
    ) {
        val hexColorMap = mutableMapOf<Int, String>()
        val height = pixelMatrix.height()

        pixelMatrix.content.forEachIndexed { rowIndex, row ->
            val y = height - 1 - rowIndex
            row.forEachIndexed { x, pixel ->
                writePixel(out, pixel, x, y, palette, hexColorMap)
            }
        }
    }

    private fun writePixel(
        out: FileOutputStream,
        pixel: PixelModel,
        x: Int,
        y: Int,
        palette: PaletteModel,
        hexColorMap: MutableMap<Int, String>,
    ) {
        if (pixel.paletteIndex < 0) {
            return
        }

        val color = getHexColor(
            palette,
            paletteIndex = pixel.paletteIndex,
            hexColorMap,
        )

        val pixelStr = """  <rect width="1" height="1" x="$x" y="$y" fill="$color" />
            |
        """.trimMargin()
        out.write(pixelStr.toByteArray())
    }

    private fun getHexColor(
        palette: PaletteModel,
        paletteIndex: Int,
        hexColorMap: MutableMap<Int, String>,
    ): String {
        hexColorMap[paletteIndex]?.let { colorHex -> return colorHex }

        val colorHex = Color.fromColorLong(palette.colors[paletteIndex]).toHexCode()
        hexColorMap[paletteIndex] = colorHex

        return colorHex
    }
}