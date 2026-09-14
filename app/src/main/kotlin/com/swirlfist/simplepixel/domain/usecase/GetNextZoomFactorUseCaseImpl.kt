package com.swirlfist.simplepixel.domain.usecase

import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GetNextZoomFactorUseCaseImpl @Inject constructor() : GetNextZoomFactorUseCase {
    override suspend fun invoke(params: GetNextZoomFactorUseCase.Params): Result<Float> {
        return Result.success(
            getNextZoomFactor(
                params.currentZoomFactor,
                params.isZoomIn,
                params.maxZoomFactor,
                params.minZoomFactor,
                params.zoomFactorStep,
            )
        )
    }

    private fun getNextZoomFactor(
        currentZoomFactor: Float,
        isZoomIn: Boolean,
        maxZoomFactor: Float,
        minZoomFactor: Float,
        zoomFactorStep: Float,
    ): Float {
        return if (isZoomIn) {
            min(maxZoomFactor, currentZoomFactor + zoomFactorStep)
        } else {
            max(minZoomFactor, currentZoomFactor - zoomFactorStep)
        }
    }
}