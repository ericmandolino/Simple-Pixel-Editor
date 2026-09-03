package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class GetBasePixelImageUseCaseImplTest {

    private lateinit var useCase: GetBasePixelImageUseCase

    @MockK
    private lateinit var basePixelImageRepository: BasePixelImageRepository

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = GetBasePixelImageUseCaseImpl(basePixelImageRepository)
    }

    @Test
    fun `when invoking the use case it returns the value from the repository`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        coEvery { basePixelImageRepository.getBasePixelImage() }.returns(pixelImage)

        // When
        val result = useCase(UseCaseParams.NoParams)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(pixelImage, result.getOrNull())
    }
}