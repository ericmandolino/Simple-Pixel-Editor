package com.swirlfist.simplepixel.presentation.main.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.presentation.launcher.ExportPixelImageLocationLauncher
import com.swirlfist.simplepixel.presentation.launcher.OpenPixelImageLocationLauncher
import com.swirlfist.simplepixel.presentation.launcher.SavePixelImageLocationLauncher
import com.swirlfist.simplepixel.presentation.main.section.ActionSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.ActionsSection
import com.swirlfist.simplepixel.presentation.main.section.CanvasSection
import com.swirlfist.simplepixel.presentation.main.section.CanvasSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.PixelImagePreviewSection
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.main.state.MainScreenLauncherState
import com.swirlfist.simplepixel.presentation.main.state.PixelImagePreviewSectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val mainScreenState = viewModel.mainScreenState.collectAsStateWithLifecycle().value
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()
    val backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange

    MainScreenLaunchers(
        mainScreenState.launcherState,
        onSelectSavePixelImageLocationResult = viewModel::onSelectSavePixelImageLocationResult,
        onSelectExportPixelImageLocationResult = viewModel::onSelectExportPixelImageLocationResult,
        onSelectOpenPixelImageLocationResult = viewModel::onSelectOpenPixelImageLocationResult,
        onSelectSavePixelImageLocationLaunched = viewModel::onSelectSavePixelImageLocationLaunched,
        onSelectExportPixelImageLocationLaunched = viewModel::onSelectExportPixelImageLocationLaunched,
        onSelectOpenPixelImageLocationLaunched = viewModel::onSelectOpenPixelImageLocationLaunched,
    )

    SupportingPaneScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        directive = scaffoldNavigator.scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        mainPane = {
            MainPane(
                canvasSectionState = mainScreenState.canvasSectionState,
                onCanvasSectionEvent = viewModel::onCanvasSectionEvent,
                scaffoldNavigator = scaffoldNavigator,
                coroutineScope = coroutineScope,
            )
        },
        supportingPane = {
            SupportingPane(
                actionsSectionState = mainScreenState.actionsSectionState,
                pixelImagePreviewSectionState = mainScreenState.pixelImagePreviewSectionState,
                onActionsSectionEvent = viewModel::onActionsSectionEvent,
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