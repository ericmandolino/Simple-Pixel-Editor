package com.swirlfist.simplepixel.presentation.launcher

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.swirlfist.simplepixel.presentation.main.screen.SelectSavePixelImageLocationError

private const val DEFAULT_SAVE_FILE_NAME = "pixelImage.pxl"

class SavePixelImageLocationLauncher {
    companion object {

        fun getLaunchIntent(): Intent {
            return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_TITLE, DEFAULT_SAVE_FILE_NAME)
            }
        }

        fun handleResult(
            activityResult: ActivityResult,
            onResult: (Result<Uri>) -> Unit,
        ) {
            val result = when (activityResult.resultCode) {
                RESULT_OK
                    -> activityResult.data?.data?.let { uri ->
                    Result.success(uri)
                } ?: Result.failure(SelectSavePixelImageLocationError(false))

                RESULT_CANCELED
                    -> Result.failure(SelectSavePixelImageLocationError(true))

                else
                    -> Result.failure(SelectSavePixelImageLocationError(false))
            }
            onResult(result)
        }
    }
}