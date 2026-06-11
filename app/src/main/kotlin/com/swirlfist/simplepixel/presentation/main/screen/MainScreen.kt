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
import com.swirlfist.simplepixel.presentation.main.section.ImageSection
import com.swirlfist.simplepixel.presentation.main.section.ImageSectionEvent
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.main.state.ImageSectionState
import com.swirlfist.simplepixel.presentation.main.state.MainScreenState

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val mainScreenState: MainScreenState = viewModel.mainScreenState.collectAsStateWithLifecycle().value
    val navigator = rememberSupportingPaneScaffoldNavigator()

    LaunchedEffect(null) {
        viewModel.init()
    }

    SupportingPaneScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        supportingPane = {
            SupportingPane(
                actionsSectionState = mainScreenState.actionsSectionState,
                onActionsSectionEvent = {},// TODO in viewmodel
            )
        },
        mainPane = {
            MainPane(
                imageSectionState = mainScreenState.imageSectionState,
                onImageSectionEvent = viewModel::onImageSectionEvent
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
    imageSectionState: ImageSectionState,
    onImageSectionEvent: (ImageSectionEvent) -> Unit,
) {
    AnimatedPane(
        modifier = modifier.safeContentPadding(),
    ) {
        ImageSection(
            state = imageSectionState,
            onEvent = onImageSectionEvent,
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