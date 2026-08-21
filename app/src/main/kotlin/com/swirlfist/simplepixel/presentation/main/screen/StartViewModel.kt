package com.swirlfist.simplepixel.presentation.main.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.simplepixel.presentation.main.state.StartScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val basePixelImageRepository: BasePixelImageRepository,
    private val openPixelImageUseCase: OpenPixelImageUseCase,
) : ViewModel() {
    private val _startScreenState = MutableStateFlow(
        value = StartScreenState()
    )
    val startScreenState = _startScreenState as StateFlow<StartScreenState>

    fun openNewImageModal() {

    }

    fun openImageSelection() {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                launcherState = startScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = true,
                ),
            )
        }
    }

    fun onSelectOpenPixelImageLocationLaunched() {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                launcherState = startScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = false,
                ),
            )
        }
    }

    fun onSelectOpenPixelImageLocationResult(
        result: Result<Uri>,
    ) {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                isLoadingImage = true,
            )
        }

        result.fold(
            onSuccess = { uri ->
                openPixelImage(uri)
            },
            onFailure = {
                _startScreenState.update { startScreenState ->
                    startScreenState.copy(
                        isLoadingImage = false,
                    )
                }
            }
        )
    }

    private fun openPixelImage(uri: Uri) {
        viewModelScope.launch {
            openPixelImageUseCase.invoke(
                OpenPixelImageUseCase.Params(
                    uri
                )
            ).fold(
                onSuccess = { loadedPixelImage ->
                    basePixelImageRepository.updateBasePixelImage(loadedPixelImage)
                    _startScreenState.update { startScreenState ->
                        startScreenState.copy(
                            isNavigateToMainExpected = true,
                        )
                    }
                },
                onFailure = {
                    _startScreenState.update { startScreenState ->
                        startScreenState.copy(
                            isLoadingImage = false,
                        )
                    }
                    // TODO: Image could not be loaded
                }
            )
        }
    }

    fun onNavigateToMainAfterImageLoaded() {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                isLoadingImage = false,
                isNavigateToMainExpected = false,
            )
        }
    }
}