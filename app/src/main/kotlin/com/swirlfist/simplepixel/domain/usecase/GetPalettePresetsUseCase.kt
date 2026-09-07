package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PaletteModel
import kotlinx.coroutines.flow.Flow

interface GetPalettePresetsUseCase : UseCase<UseCaseParams.NoParams, Flow<Map<String, PaletteModel>>>