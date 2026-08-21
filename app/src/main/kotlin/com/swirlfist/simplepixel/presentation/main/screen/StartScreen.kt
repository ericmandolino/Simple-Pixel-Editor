package com.swirlfist.simplepixel.presentation.main.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.launcher.OpenPixelImageLocationLauncher
import com.swirlfist.simplepixel.presentation.main.state.StartScreenLauncherState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun StartScreen(
    viewModel: StartViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
) {
    val startScreenState = viewModel.startScreenState.collectAsStateWithLifecycle().value

    StartScreenLaunchers(
        startScreenState.launcherState,
        onSelectOpenPixelImageLocationResult = viewModel::onSelectOpenPixelImageLocationResult,
        onSelectOpenPixelImageLocationLaunched = viewModel::onSelectOpenPixelImageLocationLaunched,
    )

    LaunchedEffect(startScreenState) {
        if (startScreenState.isNavigateToMainExpected) {
            navigateToMain()
            viewModel.onNavigateToMainAfterImageLoaded()
        }
    }

    if (startScreenState.isLoadingImage) {
        StartScreenLoadingImage()
    } else {
        StartScreenContent(
            onNewImageClick = { navigateToMain() },//viewModel::openNewImageModal,
            onLoadImageClick = viewModel::openImageSelection,
        )
    }
}

@Composable
fun StartScreenLoadingImage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.loading_image),
                style = MaterialTheme.typography.bodyMedium,
            )
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun StartScreenContent(
    onNewImageClick: () -> Unit,
    onLoadImageClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp)
    ) {
        TextButton(
            modifier = Modifier.fillMaxWidth(0.5F),
            onClick = onNewImageClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_actions_section_new_pixel_image_24dp),
                contentDescription = stringResource(R.string.cd_actions_section_button_open_pixel_image),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.new_image)
            )
        }
        TextButton(
            modifier = Modifier.fillMaxWidth(0.5F),
            onClick = onLoadImageClick
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_actions_section_open_pixel_image_24dp),
                contentDescription = stringResource(R.string.cd_actions_section_button_open_pixel_image),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.load_image)
            )
        }
    }
}

@Composable
fun StartScreenLaunchers(
    launcherState: StartScreenLauncherState,
    onSelectOpenPixelImageLocationResult: (Result<Uri>) -> Unit,
    onSelectOpenPixelImageLocationLaunched: () -> Unit,
) {
    val selectOpenPixelImageLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        OpenPixelImageLocationLauncher.handleResult(
            activityResult,
            onResult = onSelectOpenPixelImageLocationResult,
        )
    }

    LaunchedEffect(launcherState) {
        if (launcherState.launchSelectOpenPixelImage) {
            val intent = OpenPixelImageLocationLauncher.getLaunchIntent()
            selectOpenPixelImageLocationLauncher.launch(intent)
            onSelectOpenPixelImageLocationLaunched()
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun StartScreenLoadingImagePreview() {
    SimplePixelTheme {
        StartScreenLoadingImage()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun StartScreenContentPreview() {
    SimplePixelTheme {
        StartScreenContent(
            onNewImageClick = {},
            onLoadImageClick = {},
        )
    }
}