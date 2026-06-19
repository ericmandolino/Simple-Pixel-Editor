package com.swirlfist.simplepixel.presentation.main.screen

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.presentation.main.section.ActionSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.ActionsSection
import com.swirlfist.simplepixel.presentation.main.section.CanvasSection
import com.swirlfist.simplepixel.presentation.main.section.CanvasSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.PixelImagePreviewSection
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.main.state.MainScreenState
import com.swirlfist.simplepixel.presentation.main.state.PixelImagePreviewSectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val mainScreenState: MainScreenState = viewModel.mainScreenState.collectAsStateWithLifecycle().value
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()
    val backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange

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
                        .align(Alignment.End).padding(16.dp),
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