package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import kotlinx.coroutines.flow.Flow

const val MAX_UNDO_ACTIONS = 10

interface PixelImageEditorActionRepository {
    suspend fun getCurrentAction(): PixelImageEditorAction?
    suspend fun getActions(): List<PixelImageEditorAction>
    suspend fun clearActions()
    suspend fun addAction(
        action: PixelImageEditorAction,
        pixelImageResult: PixelImageModel,
        pixelSelectionResult: PixelSelectionModel? = null,
    )
    suspend fun updateLastAction(
        pixelImageResult: PixelImageModel,
        pixelSelectionResult: PixelSelectionModel? = null,
    )
    suspend fun undoAction(): Pair<PixelImageModel, PixelSelectionModel?>?
    suspend fun redoAction(): Pair<PixelImageModel, PixelSelectionModel?>?
    fun isUndoAvailable(): Flow<Boolean>
    fun isRedoAvailable(): Flow<Boolean>
}