package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.domain.error.OpenPixelImageError
import com.swirlfist.simplepixel.presentation.mapper.toPixelImageSaveModel
import com.swirlfist.simplepixel.testutil.PixelImageModelTestUtil
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class OpenPixelImageUseCaseImplTest {

    private lateinit var useCase: OpenPixelImageUseCase

    @MockK
    private lateinit var uri: Uri

    @MockK
    private lateinit var readFromFileUseCase: ReadFromFileUseCase

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = OpenPixelImageUseCaseImpl(
            readFromFileUseCase
        )
    }

    @Test
    fun `the use case invokes ReadFromFileUseCase with the expected params`() = runTest {
        // Given
        val useCaseParams = OpenPixelImageUseCase.Params(
            uri,
        )

        // When
        useCase.invoke(useCaseParams)

        // Then
        coVerify {
            readFromFileUseCase.invoke(
                match { params ->
                    params.uri == uri
                }
            )
        }
    }

    @Test
    fun `when ReadFromFileUseCase fails then the use case fails`() = runTest {
        // Given
        val useCaseParams = OpenPixelImageUseCase.Params(
            uri,
        )
        val expectedException = mockk<Exception>()
        coEvery { readFromFileUseCase.invoke(any()) }.returns(Result.failure(expectedException))

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isFailure }
        assertEquals(
            expectedException,
            (result.exceptionOrNull() as OpenPixelImageError).innerException
        )
    }

    @Test
    fun `when ReadFromFileUseCase succeeds the use case converts the content to the expected model`() =
        runTest {
            // Given
            val useCaseParams = OpenPixelImageUseCase.Params(
                uri,
            )
            val pixelImageModel = PixelImageModelTestUtil.createPixelImageModel(
                pixelImageString = """
                1 0
                1 1
                """.trimIndent()
            )
            coEvery { readFromFileUseCase.invoke(any()) }.returns(
                Result.success(
                    Json.encodeToString(pixelImageModel.toPixelImageSaveModel())
                )
            )

            // When
            val result = useCase.invoke(useCaseParams)

            // Then
            assertTrue { result.isSuccess }
            assertEquals(pixelImageModel, result.getOrNull())
        }

    @Test
    fun `when the read content cannot be converted to the expected model the use case fails`() =
        runTest {
            // Given
            val useCaseParams = OpenPixelImageUseCase.Params(
                uri,
            )
            coEvery { readFromFileUseCase.invoke(any()) }.returns(
                Result.success(
                    "invalid content"
                )
            )

            // When
            val result = useCase.invoke(useCaseParams)

            // Then
            assertFalse { result.isSuccess }
        }
}