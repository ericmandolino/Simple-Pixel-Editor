package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import com.swirlfist.simplepixel.domain.error.ExportPixelImageError
import com.swirlfist.simplepixel.presentation.toHexCode
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

class ExportPixelImageUseCaseImplTest {

    private lateinit var useCase: ExportPixelImageUseCase

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
        coEvery { writeToFileUseCase.invoke(any()) }.returns(Result.success(Unit))

        useCase = ExportPixelImageUseCaseImpl(
            writeToFileUseCase
        )
    }

    @Test
    fun `The use case invokes WriteToFileUseCase with the expected params`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val hexColor0 = Color.fromColorLong(PixelImageModelTestUtil.paletteColors[0]).toHexCode()
        val hexColor1 = Color.fromColorLong(PixelImageModelTestUtil.paletteColors[1]).toHexCode()
        val expectedContent = """
            <svg width="2" height="2" xmlns="http://www.w3.org/2000/svg">
              <rect width="1" height="1" x="0" y="1" fill="$hexColor1" />
              <rect width="1" height="1" x="1" y="1" fill="$hexColor0" />
              <rect width="1" height="1" x="0" y="0" fill="$hexColor1" />
              <rect width="1" height="1" x="1" y="0" fill="$hexColor1" />
            </svg>
        """.trimIndent()
        val useCaseParams = ExportPixelImageUseCase.Params(
            pixelImageModel,
            uri,
        )

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
    fun `When WriteToFileUseCase fails then the use case fails`() = runTest {
        // Given
        val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
            pixelImageString = testPixelImageString
        )
        val useCaseParams = ExportPixelImageUseCase.Params(
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
            (result.exceptionOrNull() as ExportPixelImageError).innerException
        )
    }
}