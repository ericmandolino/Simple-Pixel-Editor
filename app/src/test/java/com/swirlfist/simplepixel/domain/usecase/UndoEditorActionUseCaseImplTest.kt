package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.UndoEditorActionError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class UndoEditorActionUseCaseImplTest {

    private lateinit var useCase: UndoEditorActionUseCase

    @MockK
    private lateinit var pixelImageEditorActionRepository: PixelImageEditorActionRepository

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = UndoEditorActionUseCaseImpl(pixelImageEditorActionRepository)
    }

    @Test
    fun `when invoking the use case if the call to the repository returns the resulting image then the use case succeeds`() =
        runTest {
            // Given
            val pixelImage = mockk<PixelImageModel>()
            coEvery { pixelImageEditorActionRepository.undoAction() }.returns(pixelImage)

            // When
            val result = useCase.invoke(UseCaseParams.NoParams)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(pixelImage, result.getOrThrow())
        }

    @Test
    fun `when invoking the use case if the call to the repository returns a null image then the use case fails`() =
        runTest {
            // Given
            coEvery { pixelImageEditorActionRepository.undoAction() }.returns(null)

            // When
            val result = useCase.invoke(UseCaseParams.NoParams)

            // Then
            assertFalse(result.isSuccess)
            assertTrue(result.exceptionOrNull() is UndoEditorActionError)
        }
}