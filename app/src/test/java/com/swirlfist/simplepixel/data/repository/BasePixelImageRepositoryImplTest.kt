package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class BasePixelImageRepositoryImplTest {

    private lateinit var repository: BasePixelImageRepository

    @Before
    fun setup() {
        repository = BasePixelImageRepositoryImpl()
    }

    @Test
    fun `when repository is created the base pixel image is null`() = runTest {
        // Given

        // When
        val pixelImage = repository.getBasePixelImage()

        // Then
        assertNull(pixelImage)
    }

    @Test
    fun `when the image is updated then the repository returns the new image`() = runTest {
        // Given
        val pixelImage1 = mockk<PixelImageModel>()
        val pixelImage2 = mockk<PixelImageModel>()
        repository.updateBasePixelImage(pixelImage1)

        // When
        repository.updateBasePixelImage(pixelImage2)

        // Then
        assertEquals(pixelImage2, repository.getBasePixelImage())
    }
}