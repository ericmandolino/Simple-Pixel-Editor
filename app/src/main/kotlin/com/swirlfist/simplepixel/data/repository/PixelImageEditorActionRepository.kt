package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import kotlinx.coroutines.flow.Flow

interface PixelImageEditorActionRepository {
    suspend fun getCurrentAction(): PixelImageEditorAction?
    suspend fun getActions(): List<PixelImageEditorAction>
    suspend fun clearActions()
    suspend fun addAction(action: PixelImageEditorAction)
    suspend fun undoAction(): PixelImageModel?
    suspend fun redoAction(): PixelImageModel?
    fun isUndoAvailable(): Flow<Boolean>
    fun isRedoAvailable(): Flow<Boolean>
}