package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasePixelImageRepositoryImpl @Inject constructor() : BasePixelImageRepository {
    private var _basePixelImage: PixelImageModel? = null

    override fun getBasePixelImage(): PixelImageModel? {
        return _basePixelImage
    }

    override fun updateBasePixelImage(pixelImage: PixelImageModel) {
        _basePixelImage = pixelImage
    }
}