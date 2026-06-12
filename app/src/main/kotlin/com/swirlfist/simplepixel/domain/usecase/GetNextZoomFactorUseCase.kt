package com.swirlfist.simplepixel.domain.usecase

const val MAX_ZOOM_FACTOR = 2F
const val MIN_ZOOM_FACTOR = 0.5F
private const val DEFAULT_ZOOM_FACTOR_STEP = 0.1F

interface GetNextZoomFactorUseCase : UseCase<GetNextZoomFactorUseCase.Params, Float> {

    data class Params(
        val currentZoomFactor: Float,
        val isZoomIn: Boolean,
        val zoomFactorStep: Float = DEFAULT_ZOOM_FACTOR_STEP,
    ) : UseCaseParams
}