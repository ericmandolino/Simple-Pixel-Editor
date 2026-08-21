package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface BasePixelImageRepository {
    fun getBasePixelImage(): PixelImageModel?
    fun updateBasePixelImage(pixelImage: PixelImageModel)
}