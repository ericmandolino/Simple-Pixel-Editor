package com.swirlfist.simplepixel.presentation.state

import com.swirlfist.simplepixel.domain.error.ExportPixelImageError
import com.swirlfist.simplepixel.domain.error.OpenPixelImageError

data class MainScreenState(
    val canvasSectionState: CanvasSectionState,
    val actionsSectionState: ActionsSectionState,
    val pixelImagePreviewSectionState: PixelImagePreviewSectionState,
    val isShowPalette: Boolean = false,
    val isShowEditPalette: Boolean = false,
    val isShowSelectPixelImageExportFormat: Boolean = false,
    val isShowPixelImageExportSuccess: Boolean = false,
    val isBackHandlerEnabled: Boolean = true,
    val openPixelImageError: OpenPixelImageError? = null,
    val exportPixelImageError: ExportPixelImageError? = null,
    val launcherState: MainScreenLauncherState = MainScreenLauncherState(),
)

data class MainScreenLauncherState(
    val launchSelectSavePixelImage: Boolean = false,
    val launchSelectExportPixelImage: Boolean = false,
    val launchSelectOpenPixelImage: Boolean = false,
    val selectedPixelImageExportFormat: PixelImageExportFormat? = null,
)

enum class PixelImageExportFormat {
    PNG,
    SVG,
}
