package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class GetRedoEditorActionAvailableUseCaseImplTest {

    private lateinit var useCase: GetRedoEditorActionAvailableUseCase

    @MockK
    private lateinit var pixelImageEditorActionRepository: PixelImageEditorActionRepository

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = GetRedoEditorActionAvailableUseCaseImpl(pixelImageEditorActionRepository)
    }

    @Test
    fun `when invoking the use case it returns the value from the repository`() = runTest {
        // Given
        val expected = mockk<Flow<Boolean>>()
        coEvery { pixelImageEditorActionRepository.isRedoAvailable() }.returns(expected)

        // When
        val result = useCase(UseCaseParams.NoParams)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }
}