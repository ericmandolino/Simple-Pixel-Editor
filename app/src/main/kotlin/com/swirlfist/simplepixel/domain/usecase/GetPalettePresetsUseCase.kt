package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PaletteModel

interface GetPalettePresetsUseCase : UseCase<UseCaseParams.NoParams, Map<String, PaletteModel>>