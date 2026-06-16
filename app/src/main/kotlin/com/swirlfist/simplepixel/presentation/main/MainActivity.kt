package com.swirlfist.simplepixel.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCase
import com.swirlfist.simplepixel.presentation.main.screen.MainScreen
import com.swirlfist.simplepixel.presentation.main.screen.MainViewModel
import com.swirlfist.simplepixel.presentation.main.screen.MainViewModelInteraction
import com.swirlfist.simplepixel.presentation.main.screen.MainViewModelInteractionResult
import com.swirlfist.simplepixel.presentation.main.screen.SelectOpenPixelImageLocationError
import com.swirlfist.simplepixel.presentation.main.screen.SelectSavePixelImageLocationError
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val KEY_CURRENT_INTERACTION = "currentInteraction"
private const val DEFAULT_FILE_NAME = "pixelImage.pxl"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var savePixelImageUseCase: SavePixelImageUseCase
    private lateinit var mainViewModel: MainViewModel
    private var currentInteraction: MainViewModelInteraction? = null
    private var pendingInteractionResult: MainViewModelInteractionResult? = null

    private val startSelectSavePixelImageLocationForResult = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        callback = ::selectSavePixelImageLocationCallback,
    )

    private val startSelectOpenPixelImageLocationForResult = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        callback = ::selectOpenPixelImageLocationCallback,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentInteraction = savedInstanceState?.getString(KEY_CURRENT_INTERACTION)?.let { value ->
            Json.decodeFromString<MainViewModelInteraction>(value)
        }

        enableEdgeToEdge()
        setContent {
            SimplePixelTheme {
                mainViewModel = hiltViewModel()
                mainViewModel.interactions.observe(this, ::onInteraction)
                MainScreen(mainViewModel)
                pendingInteractionResult?.let { interactionResult ->
                    pendingInteractionResult = null
                    consumeInteraction(interactionResult)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            KEY_CURRENT_INTERACTION,
            currentInteraction?.let { value ->
                Json.encodeToString(value)
            }
        )
    }

    private fun onInteraction(interactions: List<MainViewModelInteraction>) {
        if (currentInteraction != null || pendingInteractionResult != null || interactions.isEmpty()) {
            return
        }

        val interaction = interactions.first()
        currentInteraction = interaction

        when (interaction) {
            is MainViewModelInteraction.SelectSavePixelImageLocationInteraction
                -> selectSavePixelImageLocation()

            MainViewModelInteraction.SelectOpenPixelImageLocationInteraction
                -> selectOpenPixelImageLocation()
        }
    }

    private fun consumeInteraction(
        result: MainViewModelInteractionResult,
    ) {
        val interaction = currentInteraction ?: return
        if (!::mainViewModel.isInitialized) {
            pendingInteractionResult = result
        } else {
            currentInteraction = null
            mainViewModel.onInteractionResult(interaction, result)
        }
    }

    private fun selectSavePixelImageLocation() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, DEFAULT_FILE_NAME)
        }

        startSelectSavePixelImageLocationForResult.launch(intent)
    }

    private fun selectSavePixelImageLocationCallback(
        activityResult: ActivityResult,
    ) {
        val interactionResult = when (activityResult.resultCode) {
            RESULT_OK
                -> activityResult.data?.data?.let { uri ->
                    Result.success(uri)
                } ?: Result.failure(SelectSavePixelImageLocationError(false))
            RESULT_CANCELED
                -> Result.failure(SelectSavePixelImageLocationError(true))
            else
                -> Result.failure(SelectSavePixelImageLocationError(false))
        }
        consumeInteraction(
            MainViewModelInteractionResult.SelectSavePixelImageLocationInteractionResult(
                interactionResult
            )
        )
    }

    private fun selectOpenPixelImageLocation() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startSelectOpenPixelImageLocationForResult.launch(intent)
    }

    private fun selectOpenPixelImageLocationCallback(
        activityResult: ActivityResult,
    ) {
        val interactionResult = when (activityResult.resultCode) {
            RESULT_OK
                -> activityResult.data?.data?.let { uri ->
                Result.success(uri)
            } ?: Result.failure(SelectOpenPixelImageLocationError(false))
            RESULT_CANCELED
                -> Result.failure(SelectOpenPixelImageLocationError(true))
            else
                -> Result.failure(SelectOpenPixelImageLocationError(false))
        }
        consumeInteraction(
            MainViewModelInteractionResult.SelectOpenPixelImageLocationInteractionResult(
                interactionResult
            )
        )
    }
}