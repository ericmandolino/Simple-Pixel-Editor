package com.swirlfist.simplepixel.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.presentation.launcher.ExportPixelImageLocationLauncher
import com.swirlfist.simplepixel.presentation.launcher.OpenPixelImageLocationLauncher
import com.swirlfist.simplepixel.presentation.launcher.SavePixelImageLocationLauncher
import com.swirlfist.simplepixel.presentation.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.section.ActionSectionEvent
import com.swirlfist.simplepixel.presentation.section.ActionsSection
import com.swirlfist.simplepixel.presentation.section.CanvasSection
import com.swirlfist.simplepixel.presentation.section.CanvasSectionEvent
import com.swirlfist.simplepixel.presentation.section.PixelImagePreviewSection
import com.swirlfist.simplepixel.presentation.section.SelectableButtonGroupDialog
import com.swirlfist.simplepixel.presentation.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.state.MainScreenLauncherState
import com.swirlfist.simplepixel.presentation.state.MainScreenState
import com.swirlfist.simplepixel.presentation.state.PixelImagePreviewSectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val mainScreenState = viewModel.mainScreenState.collectAsStateWithLifecycle().value

    MainScreenLaunchers(
        mainScreenState.launcherState,
        onSelectSavePixelImageLocationResult = viewModel::onSelectSavePixelImageLocationResult,
        onSelectExportPixelImageLocationResult = viewModel::onSelectExportPixelImageLocationResult,
        onSelectOpenPixelImageLocationResult = viewModel::onSelectOpenPixelImageLocationResult,
        onSelectSavePixelImageLocationLaunched = viewModel::onSelectSavePixelImageLocationLaunched,
        onSelectExportPixelImageLocationLaunched = viewModel::onSelectExportPixelImageLocationLaunched,
        onSelectOpenPixelImageLocationLaunched = viewModel::onSelectOpenPixelImageLocationLaunched,
    )

    if (mainScreenState.isShowPalette) {
        val openPaletteActionButtonModel = mainScreenState.actionsSectionState
            .actionModels[ActionButtonType.OpenPaletteActionButtonType] as? ActionModel.SelectableButtonGroupActionModel
        openPaletteActionButtonModel?.childButtonActionModels?.let { buttonActionModels ->
            SelectableButtonGroupDialog(
                childButtonActionModels = buttonActionModels,
                onEvent = viewModel::onActionsSectionEvent,
                onDismiss = viewModel::hidePalette,
            )
        }
    } else if (mainScreenState.isShowEditPalette) {
        val pixelImage = mainScreenState.canvasSectionState.pixelImageModel
        if (pixelImage != null) {
            PaletteEditDialog(
                originalPalette = pixelImage.paletteModel,
                pixelMatrix = pixelImage.pixelMatrixModel,
                onSaveChangesClick = viewModel::onEditPaletteChangesSaved,
                onDismiss = viewModel::hideEditPalette,
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeContentPadding()
            .padding(16.dp),
    ) {
        MainScreenContent(
            modifier = Modifier
                .fillMaxSize(),
            mainScreenState,
            onCanvasSectionEvent = viewModel::onCanvasSectionEvent,
            onActionsSectionEvent = viewModel::onActionsSectionEvent,
        )
    }
}

@Composable
fun MainScreenLaunchers(
    launcherState: MainScreenLauncherState,
    onSelectSavePixelImageLocationResult: (Result<Uri>) -> Unit,
    onSelectExportPixelImageLocationResult: (Result<Uri>) -> Unit,
    onSelectOpenPixelImageLocationResult: (Result<Uri>) -> Unit,
    onSelectSavePixelImageLocationLaunched: () -> Unit,
    onSelectExportPixelImageLocationLaunched: () -> Unit,
    onSelectOpenPixelImageLocationLaunched: () -> Unit,
) {
    val selectSavePixelImageLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        SavePixelImageLocationLauncher.handleResult(
            activityResult,
            onResult = onSelectSavePixelImageLocationResult,
        )
    }

    val selectExportPixelImageLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        ExportPixelImageLocationLauncher.handleResult(
            activityResult,
            onResult = onSelectExportPixelImageLocationResult,
        )
    }

    val selectOpenPixelImageLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        OpenPixelImageLocationLauncher.handleResult(
            activityResult,
            onResult = onSelectOpenPixelImageLocationResult,
        )
    }

    LaunchedEffect(launcherState) {
        if (launcherState.launchSelectSavePixelImage) {
            val intent = SavePixelImageLocationLauncher.getLaunchIntent()
            selectSavePixelImageLocationLauncher.launch(intent)
            onSelectSavePixelImageLocationLaunched()
        } else if (launcherState.launchSelectExportPixelImage) {
            val intent = ExportPixelImageLocationLauncher.getLaunchIntent()
            selectExportPixelImageLocationLauncher.launch(intent)
            onSelectExportPixelImageLocationLaunched()
        } else if (launcherState.launchSelectOpenPixelImage) {
            val intent = OpenPixelImageLocationLauncher.getLaunchIntent()
            selectOpenPixelImageLocationLauncher.launch(intent)
            onSelectOpenPixelImageLocationLaunched()
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    mainScreenState: MainScreenState,
    onCanvasSectionEvent: (CanvasSectionEvent) -> Unit,
    onActionsSectionEvent: (ActionSectionEvent) -> Unit,
) {
    val availableSize = LocalWindowInfo.current.containerSize

    val canvasSection = @Composable { modifier: Modifier ->
        CanvasSection(
            modifier = modifier
                .fillMaxSize(),
            state = mainScreenState.canvasSectionState,
            onEvent = onCanvasSectionEvent,
        )
    }

    val actionsSection = @Composable { modifier: Modifier ->
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionsSection(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.7F),
                state = mainScreenState.actionsSectionState,
                onEvent = onActionsSectionEvent,
            )

            PixelImagePreviewSection(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.3F),
                state = mainScreenState.pixelImagePreviewSectionState,
            )
        }
    }

    val containerModifier = modifier
        .safeContentPadding()

    if (availableSize.height >= availableSize.width) {
        Column(
            modifier = containerModifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            canvasSection(Modifier.weight(0.6F))
            actionsSection(Modifier.weight(0.4F))
        }
    } else {
        Row(
            modifier = containerModifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            canvasSection(Modifier.weight(0.6F))
            actionsSection(Modifier.weight(0.4F))
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneContent(
    modifier: Modifier = Modifier,
    mainScreenState: MainScreenState,
    onCanvasSectionEvent: (CanvasSectionEvent) -> Unit,
    onActionsSectionEvent: (ActionSectionEvent) -> Unit,
) {
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()
    val backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange

    SupportingPaneScaffold(
        modifier = modifier,
        directive = scaffoldNavigator.scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        mainPane = {
            MainPane(
                canvasSectionState = mainScreenState.canvasSectionState,
                onCanvasSectionEvent = onCanvasSectionEvent,
                scaffoldNavigator = scaffoldNavigator,
                coroutineScope = coroutineScope,
            )
        },
        supportingPane = {
            SupportingPane(
                actionsSectionState = mainScreenState.actionsSectionState,
                pixelImagePreviewSectionState = mainScreenState.pixelImagePreviewSectionState,
                onActionsSectionEvent = onActionsSectionEvent,
                scaffoldNavigator = scaffoldNavigator,
                backNavigationBehavior = backNavigationBehavior,
                coroutineScope = coroutineScope,
            )
        },
        paneExpansionState = rememberPaneExpansionState(scaffoldNavigator.scaffoldValue),
        paneExpansionDragHandle = { state ->
            val interactionSource = remember { MutableInteractionSource() }
            VerticalDragHandle(
                modifier =
                    Modifier.paneExpansionDraggable(
                        state,
                        LocalMinimumInteractiveComponentSize.current,
                        interactionSource
                    ),
                interactionSource = interactionSource,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.MainPane(
    modifier: Modifier = Modifier,
    canvasSectionState: CanvasSectionState,
    onCanvasSectionEvent: (CanvasSectionEvent) -> Unit,
    scaffoldNavigator: ThreePaneScaffoldNavigator<Any>,
    coroutineScope: CoroutineScope,
) {
    AnimatedPane(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding(),
    ) {
        if (scaffoldNavigator.isSupportingPaneHidden()) {
            Button(
                modifier = Modifier
                    .wrapContentSize(),
                onClick = {
                    coroutineScope.launch {
                        scaffoldNavigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                    }
                }
            ) {
                Text("SP") // TODO: use icon?
            }
        }
        CanvasSection(
            modifier = Modifier
                .fillMaxSize(),
            state = canvasSectionState,
            onEvent = onCanvasSectionEvent,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.SupportingPane(
    modifier: Modifier = Modifier,
    actionsSectionState: ActionsSectionState,
    pixelImagePreviewSectionState: PixelImagePreviewSectionState,
    onActionsSectionEvent: (ActionSectionEvent) -> Unit,
    scaffoldNavigator: ThreePaneScaffoldNavigator<Any>,
    backNavigationBehavior: BackNavigationBehavior,
    coroutineScope: CoroutineScope,
) {
    AnimatedPane(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding(),
    ) {
        Column(
            modifier = Modifier
                .requiredWidthIn(min = 64.dp)
        ) {
            if (scaffoldNavigator.isSupportingPaneExpanded() && scaffoldNavigator.isMainPaneHidden()) {
                Button(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp),
                    onClick = {
                        coroutineScope.launch {
                            scaffoldNavigator.navigateBack(backNavigationBehavior)
                        }
                    }
                ) {
                    Text("<") // TODO: use icon?
                }
            }

            ActionsSection(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.7F),
                state = actionsSectionState,
                onEvent = onActionsSectionEvent,
            )

            PixelImagePreviewSection(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.3F),
                state = pixelImagePreviewSectionState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun ThreePaneScaffoldNavigator<Any>.isMainPaneHidden(): Boolean {
    return isPaneVisibilityMatch(SupportingPaneScaffoldRole.Main, PaneAdaptedValue.Hidden)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun ThreePaneScaffoldNavigator<Any>.isSupportingPaneHidden(): Boolean {
    return isPaneVisibilityMatch(SupportingPaneScaffoldRole.Supporting, PaneAdaptedValue.Hidden)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun ThreePaneScaffoldNavigator<Any>.isSupportingPaneExpanded(): Boolean {
    return isPaneVisibilityMatch(SupportingPaneScaffoldRole.Supporting, PaneAdaptedValue.Expanded)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun ThreePaneScaffoldNavigator<Any>.isPaneVisibilityMatch(
    role: ThreePaneScaffoldRole,
    visibility: PaneAdaptedValue,
): Boolean {
    return scaffoldValue[role] == visibility
}