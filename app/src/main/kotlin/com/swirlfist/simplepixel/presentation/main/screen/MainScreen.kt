package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.presentation.main.section.ActionSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.ActionsSection
import com.swirlfist.simplepixel.presentation.main.section.CanvasSection
import com.swirlfist.simplepixel.presentation.main.section.CanvasSectionEvent
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.main.state.MainScreenState

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val mainScreenState: MainScreenState = viewModel.mainScreenState.collectAsStateWithLifecycle().value
    val navigator = rememberSupportingPaneScaffoldNavigator()

    SupportingPaneScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        supportingPane = {
            SupportingPane(
                actionsSectionState = mainScreenState.actionsSectionState,
                onActionsSectionEvent = viewModel::onActionsSectionEvent,
            )
        },
        mainPane = {
            MainPane(
                canvasSectionState = mainScreenState.canvasSectionState,
                onCanvasSectionEvent = viewModel::onCanvasSectionEvent
            )
        },
        paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
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
) {
    AnimatedPane(
        modifier = modifier.safeContentPadding(),
    ) {
        CanvasSection(
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
    onActionsSectionEvent: (ActionSectionEvent) -> Unit,
) {
    AnimatedPane(
        modifier = modifier.safeContentPadding(),
    ) {
        ActionsSection(
            modifier = Modifier.requiredWidthIn(min = 240.dp),
            state = actionsSectionState,
            onEvent = onActionsSectionEvent,
        )
    }
}