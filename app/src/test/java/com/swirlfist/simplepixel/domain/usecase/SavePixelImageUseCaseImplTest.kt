package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
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
            writeToFileUseCase
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
        coEvery { writeToFileUseCase.invoke(any()) }.returns(Result.success(Unit))

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        coVerify {
            writeToFileUseCase.invoke(
                match { params ->
                    params.uri == uri && params.content == expectedContent
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
        coEvery { writeToFileUseCase.invoke(any()) }.returns(Result.failure(expectedException))

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isFailure }
        assertEquals(
            expectedException,
            (result.exceptionOrNull() as SavePixelImageError).innerException
        )
    }
}