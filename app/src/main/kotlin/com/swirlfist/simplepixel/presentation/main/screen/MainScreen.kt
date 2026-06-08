package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.presentation.main.section.ImageSection
import com.swirlfist.simplepixel.presentation.main.state.MainScreenState

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val mainScreenState: MainScreenState = viewModel.mainScreenState.collectAsStateWithLifecycle().value
    val imageSectionState = mainScreenState.imageSectionState

    LaunchedEffect(null) {
        viewModel.init()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ImageSection(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            state = imageSectionState,
            onEvent = viewModel::onImageSectionEvent,
        )
    }
}