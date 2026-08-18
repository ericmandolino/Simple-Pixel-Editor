package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.testutil.PixelImageModelTestUtil
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class MoveImageUseCaseImplTest {

    private lateinit var useCase : MoveImageUseCase
    private val testPixelImageString = """
    1 2 2 1
    1 1 2 0
    1 2 1 1
    1 1 3 3
    """.trimIndent()

    @Before
    fun setup() {
        useCase = MoveImageUseCaseImpl()
    }

    @Test
    fun `when moving image up all pixels move up and empty pixels fill the bottom`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val useCaseParams = MoveImageUseCase.Params(
            pixelImageModel,
            MoveDirection.UP,
        )
        val expected = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = ("""
                -1 -1 -1 -1
                1 2 2 1
                1 1 2 0
                1 2 1 1
                """.trimIndent()
                    ),
            paletteSize = 4,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `when moving image down all pixels move down and empty pixels fill the top`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val useCaseParams = MoveImageUseCase.Params(
            pixelImageModel,
            MoveDirection.DOWN,
        )
        val expected = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = ("""
                1 1 2 0
                1 2 1 1
                1 1 3 3
                -1 -1 -1 -1
                """.trimIndent()
                    ),
            paletteSize = 4,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `when moving image left all pixels move left and empty pixels fill the right`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val useCaseParams = MoveImageUseCase.Params(
            pixelImageModel,
            MoveDirection.LEFT,
        )
        val expected = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = ("""
                2 2 1 -1
                1 2 0 -1
                2 1 1 -1
                1 3 3 -1
                """.trimIndent()
                    ),
            paletteSize = 4,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `when moving image right all pixels move right and empty pixels fill the left`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val useCaseParams = MoveImageUseCase.Params(
            pixelImageModel,
            MoveDirection.RIGHT,
        )
        val expected = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = ("""
                -1 1 2 2
                -1 1 1 2
                -1 1 2 1
                -1 1 1 3
                """.trimIndent()
                    ),
            paletteSize = 4,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(expected, result.getOrNull())
    }
}