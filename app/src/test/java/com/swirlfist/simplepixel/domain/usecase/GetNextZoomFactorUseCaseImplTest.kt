package com.swirlfist.simplepixel.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class GetNextZoomFactorUseCaseImplTest {

    private lateinit var useCase: GetNextZoomFactorUseCase

    @Before
    fun setup() {
        useCase = GetNextZoomFactorUseCaseImpl()
    }

    @Test
    fun `zooming in increases zoom factor by step value`() = runTest {
        // Given
        val useCaseParams = GetNextZoomFactorUseCase.Params(
            currentZoomFactor = 1F,
            isZoomIn = true,
            zoomFactorStep = 0.25F,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(1.25F, result.getOrNull())
    }

    @Test
    fun `zooming in caps at max zoom factor`() = runTest {
        // Given
        val useCaseParams = GetNextZoomFactorUseCase.Params(
            currentZoomFactor = MAX_ZOOM_FACTOR - 0.1F,
            isZoomIn = true,
            zoomFactorStep = 0.25F,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(MAX_ZOOM_FACTOR, result.getOrNull())
    }

    @Test
    fun `zooming out decreases zoom factor by step value`() = runTest {
        // Given
        val useCaseParams = GetNextZoomFactorUseCase.Params(
            currentZoomFactor = 1F,
            isZoomIn = false,
            zoomFactorStep = 0.25F,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(0.75F, result.getOrNull())
    }

    @Test
    fun `zooming out caps at min zoom factor`() = runTest {
        // Given
        val useCaseParams = GetNextZoomFactorUseCase.Params(
            currentZoomFactor = MIN_ZOOM_FACTOR + 0.1F,
            isZoomIn = false,
            zoomFactorStep = 0.25F,
        )

        // When
        val result = useCase.invoke(useCaseParams)

        // Then
        assertTrue { result.isSuccess }
        assertEquals(MIN_ZOOM_FACTOR, result.getOrNull())
    }
}