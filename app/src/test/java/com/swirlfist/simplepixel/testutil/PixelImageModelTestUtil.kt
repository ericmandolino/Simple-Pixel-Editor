package com.swirlfist.simplepixel.testutil

import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import kotlin.math.max

class PixelImageModelTestUtil {

    companion object {
        fun createPixelImageModel(pixelImageString: String): PixelImageModel {
            var maxPaletteIndex = 0
            val rows = mutableListOf<List<PixelModel>>()

            val stringRows = pixelImageString.split("\n")
            stringRows.forEach { stringRow ->
                val stringPixels = stringRow.split(" ")
                val pixelModelRow = mutableListOf<PixelModel>()
                stringPixels.forEach { stringPixel ->
                    val paletteIndex = stringPixel.toInt()
                    pixelModelRow.add(PixelModel(paletteIndex))
                    maxPaletteIndex = max(maxPaletteIndex, paletteIndex)
                }

                rows.add(pixelModelRow.toList())
            }

            val colors = mutableListOf<Long>().apply {
                for (i in 0..maxPaletteIndex) {
                    add(i.toLong())
                }
            }

            return PixelImageModel(
                pixelMatrixModel = PixelMatrixModel(
                    content = rows.toList()
                ),
                paletteModel = PaletteModel(
                    colors = colors
                )
            )
        }
    }
}