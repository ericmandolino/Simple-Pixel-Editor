package com.swirlfist.simplepixel.presentation.usecase

import android.content.Context
import android.net.Uri
import com.swirlfist.simplepixel.domain.error.WriteToFileError
import com.swirlfist.simplepixel.domain.usecase.WriteToFileUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileOutputStream
import javax.inject.Inject

class WriteToFileUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : WriteToFileUseCase {
    override suspend fun invoke(params: WriteToFileUseCase.Params): Result<Unit> {
        return try {
            writeToFile(
                params.content,
                params.uri,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(WriteToFileError(e))
        }
    }

    private fun writeToFile(
        content: String,
        uri: Uri,
    ) {
        val contentResolver = applicationContext.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
            FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                outputStream.write(content.toByteArray())
            }
        }
    }
}