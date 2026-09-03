package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.UpdatePixelError
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.testutil.PixelImageModelTestUtil
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class UpdatePixelColorUseCaseImplTest {

    private lateinit var useCase: UpdatePixelColorUseCase
    private val testPixelImageString = """
    1 2 2 1
    1 1 2 0
    1 2 1 1
    1 1 3 3
    """.trimIndent()

    @MockK
    private lateinit var pixelImageEditorActionRepository: PixelImageEditorActionRepository

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = UpdatePixelColorUseCaseImpl(pixelImageEditorActionRepository)
    }

    @Test
    fun `when the pixel coordinates are within the image then the palette index for that pixel gets updated`() =
        runTest {
            // Given
            val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
                pixelImageString = testPixelImageString
            )
            val x = 1
            val y = 1
            val useCaseParams = UpdatePixelColorUseCase.Params(
                pixelImageModel,
                x,
                y,
                paletteIndex = 3,
            )
            val expected = PixelImageModelTestUtil.createPixelImageModel(
                pixelImageString = ("""
                1 2 2 1
                1 3 2 0
                1 2 1 1
                1 1 3 3
                """.trimIndent()
                        )
            )

            // When
            val result = useCase(useCaseParams)

            // Then
            assertTrue { result.isSuccess }
            assertEquals(expected, result.getOrNull())
        }

    @Test
    fun `when the pixel coordinates are outside the image then the use case fails`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val x = 6
        val y = 1
        val useCaseParams = UpdatePixelColorUseCase.Params(
            pixelImageModel,
            x,
            y,
            paletteIndex = 3,
        )

        // When
        val result = useCase(useCaseParams)

        // Then
        assertFalse { result.isSuccess }
        assertTrue { result.exceptionOrNull() is UpdatePixelError }
    }

    @Test
    fun `when the use case fails the undo action is not added`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val x = 6
        val y = 1
        val useCaseParams = UpdatePixelColorUseCase.Params(
            pixelImageModel,
            x,
            y,
            paletteIndex = 3,
        )

        // When
        useCase(useCaseParams)

        // Then
        coVerify(exactly = 0) {
            pixelImageEditorActionRepository.addAction(any(), any())
        }
    }

    @Test
    fun `when the use case succeeds the undo action is added`() =
        runTest {
            // Given
            val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
                pixelImageString = testPixelImageString
            )
            val x = 1
            val y = 1
            val useCaseParams = UpdatePixelColorUseCase.Params(
                pixelImageModel,
                x,
                y,
                paletteIndex = 3,
            )

            // When
            val result = useCase(useCaseParams)

            // Then
            coVerify(exactly = 1) {
                pixelImageEditorActionRepository.addAction(
                    PixelImageEditorAction.ApplyPixelColorAction(
                        pixelImageModel,
                        x,
                        y,
                        paletteIndex = 3,
                    ),
                    result.getOrThrow(),
                )
            }
        }
}