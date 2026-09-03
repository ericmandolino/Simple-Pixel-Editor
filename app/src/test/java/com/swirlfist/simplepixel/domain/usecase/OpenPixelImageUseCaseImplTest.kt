package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
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

    @MockK
    private lateinit var pixelImageEditorActionRepository: PixelImageEditorActionRepository

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = OpenPixelImageUseCaseImpl(
            readFromFileUseCase,
            pixelImageEditorActionRepository,
        )
    }

    @Test
    fun `the use case invokes ReadFromFileUseCase with the expected params`() = runTest {
        // Given
        val useCaseParams = OpenPixelImageUseCase.Params(
            uri,
        )

        // When
        useCase(useCaseParams)

        // Then
        coVerify {
            readFromFileUseCase(
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
        coEvery { readFromFileUseCase(any()) }.returns(Result.failure(expectedException))

        // When
        val result = useCase(useCaseParams)

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
            coEvery { readFromFileUseCase(any()) }.returns(
                Result.success(
                    Json.encodeToString(pixelImageModel.toPixelImageSaveModel())
                )
            )

            // When
            val result = useCase(useCaseParams)

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
            coEvery { readFromFileUseCase(any()) }.returns(
                Result.success(
                    "invalid content"
                )
            )

            // When
            val result = useCase(useCaseParams)

            // Then
            assertFalse { result.isSuccess }
        }

    @Test
    fun `when the use case fails the editor actions are not cleared`() = runTest {
        // Given
        val useCaseParams = OpenPixelImageUseCase.Params(
            uri,
        )
        val expectedException = mockk<Exception>()
        coEvery { readFromFileUseCase(any()) }.returns(Result.failure(expectedException))

        // When
        useCase(useCaseParams)

        // Then
        coVerify(exactly = 0) {
            pixelImageEditorActionRepository.clearActions()
        }
    }

    @Test
    fun `when the use case succeeds the editor actions are cleared`() =
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
            coEvery { readFromFileUseCase(any()) }.returns(
                Result.success(
                    Json.encodeToString(pixelImageModel.toPixelImageSaveModel())
                )
            )

            // When
            useCase(useCaseParams)

            // Then
            coVerify(exactly = 1) {
                pixelImageEditorActionRepository.clearActions()
            }
        }
}