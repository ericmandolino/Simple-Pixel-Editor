package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import kotlinx.coroutines.flow.Flow

const val MAX_UNDO_ACTIONS = 10

interface PixelImageEditorActionRepository {
    suspend fun getCurrentAction(): PixelImageEditorAction?
    suspend fun getActions(): List<PixelImageEditorAction>
    suspend fun clearActions()
    suspend fun addAction(
        action: PixelImageEditorAction,
        actionContextResult: PixelImageEditorActionContext,
    )
    suspend fun updateLastAction(
        actionContextResult: PixelImageEditorActionContext,
    )
    suspend fun undoAction(): PixelImageEditorActionContext?
    suspend fun redoAction(): PixelImageEditorActionContext?
    fun isUndoAvailable(): Flow<Boolean>
    fun isRedoAvailable(): Flow<Boolean>
}