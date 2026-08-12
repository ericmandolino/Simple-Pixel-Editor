package com.swirlfist.simplepixel.domain.usecase

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
import javax.inject.Inject

class ExportPixelImageUseCaseImpl @Inject constructor(
    private val writeToFileUseCase: WriteToFileUseCase,
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

    private suspend fun exportPixelImage(
        pixelImageModel: PixelImageModel,
        uri: Uri,
    ) {
        val stringBuilder = StringBuilder()
        val pixelMatrix = pixelImageModel.pixelMatrixModel
        writeHeader(
            stringBuilder,
            width = pixelMatrix.width(),
            height = pixelMatrix.height(),
        )
        writeContent(
            stringBuilder,
            pixelMatrix,
            palette = pixelImageModel.paletteModel,
        )
        writeFooter(stringBuilder)

        writeToFileUseCase.invoke(
            WriteToFileUseCase.Params(
                content = stringBuilder.toString(),
                uri,
            )
        ).fold(
            onSuccess = {},
            onFailure = { throwable -> throw throwable }
        )
    }

    private fun writeHeader(
        stringBuilder: StringBuilder,
        width: Int,
        height: Int,
    ) {
        val header = """<svg width="$width" height="$height" xmlns="http://www.w3.org/2000/svg">
            |
        """.trimMargin()
        stringBuilder.append(header)
    }

    private fun writeFooter(
        stringBuilder: StringBuilder,
    ) {
        stringBuilder.append("</svg>")
    }

    private fun writeContent(
        stringBuilder: StringBuilder,
        pixelMatrix: PixelMatrixModel,
        palette: PaletteModel,
    ) {
        val hexColorMap = mutableMapOf<Int, String>()
        val height = pixelMatrix.height()

        pixelMatrix.content.forEachIndexed { rowIndex, row ->
            val y = height - 1 - rowIndex
            row.forEachIndexed { x, pixel ->
                writePixel(stringBuilder, pixel, x, y, palette, hexColorMap)
            }
        }
    }

    private fun writePixel(
        stringBuilder: StringBuilder,
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
        stringBuilder.append(pixelStr)
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