package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface BasePixelImageRepository {
    suspend fun getBasePixelImage(): PixelImageModel?
    suspend fun updateBasePixelImage(pixelImage: PixelImageModel)
}