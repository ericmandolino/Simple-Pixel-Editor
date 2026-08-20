package com.swirlfist.simplepixel.presentation.launcher

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.swirlfist.simplepixel.presentation.main.screen.SelectOpenPixelImageLocationError

class OpenPixelImageLocationLauncher {
    companion object {

        fun getLaunchIntent(): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
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
                } ?: Result.failure(SelectOpenPixelImageLocationError(false))

                RESULT_CANCELED
                    -> Result.failure(SelectOpenPixelImageLocationError(true))

                else
                    -> Result.failure(SelectOpenPixelImageLocationError(false))
            }
            onResult(result)
        }
    }
}