package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import javax.inject.Inject

class GetRectangleSelectionActionTypeUseCaseImpl @Inject constructor() : GetRectangleSelectionActionTypeUseCase {
    override suspend fun invoke(params: GetRectangleSelectionActionTypeUseCase.Params): Result<RectangleSelectionActionType> {
        return Result.success(
            getRectangleSelectionActionType(
                pixelImageModel = params.pixelImageModel,
                x = params.x,
                y = params.y,
            )
        )
    }

    private fun getRectangleSelectionActionType(
        pixelImageModel: PixelImageModel,
        x: Int,
        y: Int,
    ) : RectangleSelectionActionType {
        return RectangleSelectionActionType.NEW_SELECTION
    }
}