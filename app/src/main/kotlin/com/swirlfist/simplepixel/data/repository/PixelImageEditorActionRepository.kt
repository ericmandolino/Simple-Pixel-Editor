package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import kotlinx.coroutines.flow.Flow

const val MAX_UNDO_ACTIONS = 10

interface PixelImageEditorActionRepository {
    suspend fun getCurrentAction(): PixelImageEditorAction?
    suspend fun getActions(): List<PixelImageEditorAction>
    suspend fun clearActions()
    suspend fun addAction(
        action: PixelImageEditorAction,
        pixelImageResult: PixelImageModel,
    )

    suspend fun undoAction(): PixelImageModel?
    suspend fun redoAction(): PixelImageModel?
    fun isUndoAvailable(): Flow<Boolean>
    fun isRedoAvailable(): Flow<Boolean>
}