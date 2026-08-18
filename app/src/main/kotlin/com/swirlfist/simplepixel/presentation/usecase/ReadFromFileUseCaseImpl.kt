package com.swirlfist.simplepixel.presentation.usecase

import android.content.Context
import android.net.Uri
import com.swirlfist.simplepixel.domain.error.ReadFromFileError
import com.swirlfist.simplepixel.domain.usecase.ReadFromFileUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class ReadFromFileUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : ReadFromFileUseCase {
    override suspend fun invoke(params: ReadFromFileUseCase.Params): Result<String> {
        return try {
            val content = readFromFile(params.uri)
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(ReadFromFileError(e))
        }
    }

    private fun readFromFile(
        uri: Uri,
    ): String {
        val contentResolver = applicationContext.contentResolver

        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }

        return stringBuilder.toString()
    }
}