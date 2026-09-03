package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class UpdateBasePixelImageUseCaseImplTest {

    private lateinit var useCase: UpdateBasePixelImageUseCase

    @MockK
    private lateinit var basePixelImageRepository: BasePixelImageRepository

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Before
    fun setup() {
        useCase = UpdateBasePixelImageUseCaseImpl(basePixelImageRepository)
    }

    @Test
    fun `when invoking the use case it updates the pixel image via a repository call`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()

        // When
        val result = useCase(
            UpdateBasePixelImageUseCase.Params(pixelImage)
        )

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            basePixelImageRepository.updateBasePixelImage(pixelImage)
        }
    }
}