package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface RedoEditorActionUseCase : UseCase<UseCaseParams.NoParams, Pair<PixelImageModel, PixelSelectionModel?>>