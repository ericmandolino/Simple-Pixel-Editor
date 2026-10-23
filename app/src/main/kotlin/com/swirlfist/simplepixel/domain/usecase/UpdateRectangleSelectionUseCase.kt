package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface UpdateRectangleSelectionUseCase : UseCase<UpdateRectangleSelectionUseCase.Params, PixelSelectionModel> {

    data class Params(
        val pixelSelectionModel: PixelSelectionModel,
        val x: Int,
        val y: Int,
        val actionType: RectangleSelectionActionType,
        val isSameAction: Boolean,
    ) : UseCaseParams
}