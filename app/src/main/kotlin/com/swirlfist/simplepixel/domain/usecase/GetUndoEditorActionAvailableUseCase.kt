package com.swirlfist.simplepixel.domain.usecase

import kotlinx.coroutines.flow.Flow

interface GetUndoEditorActionAvailableUseCase : UseCase<UseCaseParams.NoParams, Flow<Boolean>>