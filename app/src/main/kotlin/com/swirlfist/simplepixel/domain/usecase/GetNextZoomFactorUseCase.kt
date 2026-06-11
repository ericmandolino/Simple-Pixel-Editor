package com.swirlfist.simplepixel.domain.usecase

private const val DEFAULT_ZOOM_FACTOR_STEP = 0.1F

interface GetNextZoomFactorUseCase : UseCase<GetNextZoomFactorUseCase.Params, Float> {

    data class Params(
        val currentZoomFactor: Float,
        val isZoomIn: Boolean,
        val zoomFactorStep: Float = DEFAULT_ZOOM_FACTOR_STEP,
    ) : UseCaseParams
}