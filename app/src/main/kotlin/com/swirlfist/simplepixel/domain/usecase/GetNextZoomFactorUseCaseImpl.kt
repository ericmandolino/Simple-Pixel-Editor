package com.swirlfist.simplepixel.domain.usecase

import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

private const val MAX_ZOOM_FACTOR = 4F
private const val MIN_ZOOM_FACTOR = 0.1F

class GetNextZoomFactorUseCaseImpl @Inject constructor() : GetNextZoomFactorUseCase {
    override suspend fun invoke(params: GetNextZoomFactorUseCase.Params): Result<Float> {
        return Result.success(
            getNextZoomFactor(
                params.currentZoomFactor,
                params.isZoomIn,
                params.zoomFactorStep,
            )
        )
    }

    private fun getNextZoomFactor(
        currentZoomFactor: Float,
        isZoomIn: Boolean,
        zoomFactorStep: Float,
    ): Float {
        return if (isZoomIn) {
            min(MAX_ZOOM_FACTOR, currentZoomFactor + zoomFactorStep)
        } else {
            max(MIN_ZOOM_FACTOR, currentZoomFactor - zoomFactorStep)
        }
    }
}