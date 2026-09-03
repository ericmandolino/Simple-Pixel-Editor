package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class ClearEditorActionsUseCaseImplTest {

    private lateinit var useCase: ClearEditorActionsUseCase

    @MockK
    private lateinit var pixelImageEditorActionRepository: PixelImageEditorActionRepository

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = ClearEditorActionsUseCaseImpl(pixelImageEditorActionRepository)
    }

    @Test
    fun `when invoking the use case it clears editor actions via a repository call`() = runTest {
        // Given

        // When
        val result = useCase(UseCaseParams.NoParams)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            pixelImageEditorActionRepository.clearActions()
        }
    }
}