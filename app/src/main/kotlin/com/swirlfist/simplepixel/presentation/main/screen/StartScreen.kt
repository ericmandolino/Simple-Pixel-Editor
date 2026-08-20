package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun StartScreen(
    viewModel: StartViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
) {
    StartScreenContent(
        onNewImageClick = onNavigateToMain,//viewModel::openNewImageModal,
        onLoadImageClick = viewModel::openImageSelection,
    )
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