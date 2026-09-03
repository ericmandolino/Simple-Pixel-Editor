package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import com.swirlfist.simplepixel.domain.error.SavePixelImageError
import com.swirlfist.simplepixel.testutil.PixelImageModelTestUtil
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class SavePixelImageUseCaseImplTest {

    private lateinit var useCase: SavePixelImageUseCase

    @MockK
    private lateinit var uri: Uri

    @MockK
    private lateinit var writeToFileUseCase: WriteToFileUseCase

    @MockK
    private lateinit var basePixelImageRepository: BasePixelImageRepository

    private val testPixelImageString = """
    1 0
    1 1
    """.trimIndent()

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = SavePixelImageUseCaseImpl(
            writeToFileUseCase,
            basePixelImageRepository,
        )
    }

    @Test
    fun `the use case invokes WriteToFileUseCase with the expected params`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val color0 = PixelImageModelTestUtil.paletteColors[0]
        val color1 = PixelImageModelTestUtil.paletteColors[1]
        val expectedContent = """
            {"pixels":[[1,0],[1,1]],"palette":[$color0,$color1]}
        """.trimIndent()
        val useCaseParams = SavePixelImageUseCase.Params(
            pixelImageModel,
            uri,
        )
        coEvery { writeToFileUseCase(any()) }.returns(Result.success(Unit))

        // When
        val result = useCase(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        coVerify {
            writeToFileUseCase(
                match { params ->
                    params.uri == uri && params.content == expectedContent
                }
            )
        }
    }

    @Test
    fun `when WriteToFileUseCase succeeds then the image is updated in the repository`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val useCaseParams = SavePixelImageUseCase.Params(
            pixelImageModel,
            uri,
        )
        coEvery { writeToFileUseCase(any()) }.returns(Result.success(Unit))

        // When
        val result = useCase(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        coVerify {
            basePixelImageRepository.updateBasePixelImage(
                match { pixelImage ->
                    pixelImage == pixelImageModel
                }
            )
        }
    }

    @Test
    fun `when WriteToFileUseCase fails then the use case fails`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val useCaseParams = SavePixelImageUseCase.Params(
            pixelImageModel,
            uri,
        )
        val expectedException = mockk<Exception>()
        coEvery { writeToFileUseCase(any()) }.returns(Result.failure(expectedException))

        // When
        val result = useCase(useCaseParams)

        // Then
        assertTrue { result.isFailure }
        assertEquals(
            expectedException,
            (result.exceptionOrNull() as SavePixelImageError).innerException
        )
    }
}