package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PalettePresetModel
import kotlinx.coroutines.flow.Flow

interface GetPalettePresetsUseCase : UseCase<UseCaseParams.NoParams, Flow<List<PalettePresetModel>>>