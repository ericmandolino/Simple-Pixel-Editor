package com.swirlfist.simplepixel.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PixelImageModel(
    val pixelMatrixModel: PixelMatrixModel,
    val paletteModel: PaletteModel,
) {
    companion object {
        fun createEmpty(
            width: Int,
            height: Int,
            colors: List<Long>,
        ): PixelImageModel {
            val rows = mutableListOf<List<PixelModel>>()
            repeat(height) {
                val row = mutableListOf<PixelModel>()
                repeat(width) {
                    row.add(
                        PixelModel(-1)
                    )
                }
                rows.add(row)
            }

            return PixelImageModel(
                pixelMatrixModel = PixelMatrixModel(
                    content = rows
                ),
                paletteModel = PaletteModel(colors),
            )
        }

    }
}