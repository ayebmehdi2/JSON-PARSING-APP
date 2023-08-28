package com.itshedi.jsonparsereditor.jsonStuff

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.InputStreamReader


@Composable
fun FilePicker(onFileContent: (String) -> Unit) {
    val context = LocalContext.current

    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                loadFileFromUri(uri = it, context = context)?.let { content ->
                    onFileContent(
                        content
                    )
                }
            }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { filePicker.launch(arrayOf("application/json")) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Scegli un file")
        }

    }
}

fun loadFileFromUri(uri: Uri, context: Context): String? {
    try{
        val inputStream = context.contentResolver.openInputStream(uri)

        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var receiveString: String? = ""
            val stringBuilder = StringBuilder()
            while (bufferedReader.readLine().also { receiveString = it } != null) {
                stringBuilder.appendLine(receiveString)
            }
            return stringBuilder.toString()
        }
    }catch (e:Exception){
        Log.i("COOLTAG",e.message.toString())
    }

    return null
}