package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PixelImageEditorActionRepositoryImpl : PixelImageEditorActionRepository {
    private var _actions = mutableListOf<PixelImageEditorAction>()
    private var _currentActionIndex: Int = -1
    private var _undoAvailableFlow = MutableStateFlow(false)
    private var _redoAvailableFlow = MutableStateFlow(false)

    override suspend fun getCurrentAction(): PixelImageEditorAction? {
        return _actions.getOrNull(_currentActionIndex)
    }

    override suspend fun getActions(): List<PixelImageEditorAction> {
        return _actions.toList()
    }

    override suspend fun clearActions() {
        _actions.clear()
        _currentActionIndex = -1
        updateAvailableOperations()
    }

    override suspend fun addAction(action: PixelImageEditorAction) {
        clearActionsFromIndex(_currentActionIndex + 1)
        _actions.add(action)
        _currentActionIndex = _actions.size - 1
        updateAvailableOperations()
    }

    override suspend fun undoAction(): PixelImageModel? {
        if (_currentActionIndex !in _actions.indices) {
            return null
        }

        val pixelImage = _actions[_currentActionIndex].pixelImage
        _currentActionIndex--
        updateAvailableOperations()

        return pixelImage
    }

    override suspend fun redoAction(): PixelImageModel? {
        val newCurrentActionIndex = _currentActionIndex + 1
        return if (newCurrentActionIndex !in _actions.indices) {
            null
        } else {
            _currentActionIndex = newCurrentActionIndex
            updateAvailableOperations()
            _actions[_currentActionIndex].pixelImage
        }
    }

    override fun isUndoAvailable(): Flow<Boolean> {
        return _undoAvailableFlow.asStateFlow()
    }

    override fun isRedoAvailable(): Flow<Boolean> {
        return _redoAvailableFlow.asStateFlow()
    }

    private fun clearActionsFromIndex(actionIndex: Int) {
        if (actionIndex !in _actions.indices) {
            return
        }

        _actions = _actions.subList(0, actionIndex)
        _currentActionIndex = _actions.size - 1
    }

    private fun updateAvailableOperations() {
        _undoAvailableFlow.update {
            _currentActionIndex in _actions.indices
        }
        _redoAvailableFlow.update {
            (_currentActionIndex + 1) in _actions.indices
        }
    }
}