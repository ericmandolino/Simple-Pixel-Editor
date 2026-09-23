package com.swirlfist.simplepixel.presentation.launcher

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.swirlfist.simplepixel.presentation.screen.SelectExportPixelImageLocationError
import com.swirlfist.simplepixel.presentation.state.PixelImageExportFormat

private const val DEFAULT_EXPORT_FILE_NAME = "pixelImage"
private const val EXPORT_FILE_EXTENSION_PNG = ".png"
private const val EXPORT_FILE_EXTENSION_SVG = ".svg"

class ExportPixelImageLocationLauncher {
    companion object {

        fun getLaunchIntent(
            exportFormat: PixelImageExportFormat,
        ): Intent {
            val fileExtension = when(exportFormat) {
                PixelImageExportFormat.PNG -> EXPORT_FILE_EXTENSION_PNG
                PixelImageExportFormat.SVG -> EXPORT_FILE_EXTENSION_SVG
            }

            return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_TITLE, "$DEFAULT_EXPORT_FILE_NAME$fileExtension")
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
                } ?: Result.failure(SelectExportPixelImageLocationError(false))

                RESULT_CANCELED
                    -> Result.failure(SelectExportPixelImageLocationError(true))

                else
                    -> Result.failure(SelectExportPixelImageLocationError(false))
            }
            onResult(result)
        }
    }
}