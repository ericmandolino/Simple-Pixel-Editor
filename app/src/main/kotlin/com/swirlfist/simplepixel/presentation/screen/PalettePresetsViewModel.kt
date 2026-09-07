package com.swirlfist.simplepixel.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.domain.usecase.GetPalettePresetsUseCase
import com.swirlfist.simplepixel.domain.usecase.UseCaseParams
import com.swirlfist.simplepixel.presentation.state.PalettePresetsDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PalettePresetsViewModel @Inject constructor(
    private val getPalettePresetsUseCase: GetPalettePresetsUseCase,
) : ViewModel() {
    private val _palettePresetsDialogState = MutableStateFlow(
        value = PalettePresetsDialogState(
            isLoadingPresets = true,
        )
    )
    val palettePresetsDialogState = _palettePresetsDialogState.asStateFlow()

    init {
        viewModelScope.launch {
            getPalettePresetsUseCase(UseCaseParams.NoParams).getOrNull()?.let { palettePresetsFlow ->
                palettePresetsFlow.collect { palettePresets ->
                    _palettePresetsDialogState.update { state ->
                        state.copy(
                            palettePresets = palettePresets,
                            isLoadingPresets = false,
                        )
                    }
                }
            }
        }
    }
}