package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.presentation.getPixelAt
import com.swirlfist.simplepixel.testutil.PixelImageModelTestUtil
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class ApplyBucketUseCaseImplTest {

    private lateinit var useCase: ApplyBucketUseCase
    private val testPixelImageString = """
    1 2 2 1
    1 1 2 0
    1 2 1 1
    1 1 3 3
    """.trimIndent()

    @Before
    fun setup() {
        useCase = ApplyBucketUseCaseImpl()
    }

    @Test
    fun `when the palette index is the same the image does not change`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val x = 1
        val y = 1
        val useCaseParams = ApplyBucketUseCase.Params(
            pixelImageModel,
            x,
            y,
            paletteIndex = pixelImageModel.getPixelAt(x, y).paletteIndex,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(pixelImageModel, result.getOrNull())
    }

    @Test
    fun `when the pixel is isolated then only that pixel is updated`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val x = 1
        val y = 1
        val useCaseParams = ApplyBucketUseCase.Params(
            pixelImageModel,
            x,
            y,
            paletteIndex = 0,
        )
        val expected = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = ("""
                0 2 2 1
                0 0 2 0
                0 2 1 1
                0 0 3 3
                """.trimIndent()
                    )
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `when the pixel is not isolated then all pixels in the group are updated`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val x = 1
        val y = 2
        val useCaseParams = ApplyBucketUseCase.Params(
            pixelImageModel,
            x,
            y,
            paletteIndex = 0,
        )
        val expected = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = ("""
                1 2 2 1
                1 1 2 0
                1 0 1 1
                1 1 3 3
                """.trimIndent()
                    )
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(expected, result.getOrNull())
    }
}