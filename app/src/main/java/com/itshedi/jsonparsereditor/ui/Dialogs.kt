package com.itshedi.jsonparsereditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

//type 0 for text, 1 for text
@Composable
fun InputDialog(type:Int, onConfirm: (String) -> Unit, onDismissRequest: () -> Unit) {
    var value by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        val focusRequester = remember { FocusRequester() }
        Column(modifier = Modifier
            .wrapContentSize()
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(size = 16.dp)
            )) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                value = value, onValueChange = {
                value = it
            },
                placeholder = { when(type){
                    1 -> Text("Inserisci valore")
                    else -> Text("Inserisci testo")
                }},
                keyboardOptions = when(type){
                    1-> KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    else -> KeyboardOptions.Default
                },
            )
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = "Cancel")
                }

                TextButton(
                    onClick = {
                        onConfirm(value)
                        onDismissRequest()
                    },
                    enabled = value.isNotBlank()
                ) {
                    Text(text = "OK")
                }
            }
        }
        LaunchedEffect(true){
            delay(400)
            focusRequester.requestFocus()
        }
    }
}


//type 0 for text, 1 for text
@Composable
fun ListPickerDialog(items:List<String>, onSelect: (String) -> Unit, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        Column(modifier = Modifier
            .wrapContentSize()
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(size = 16.dp)
            )) {
            LazyColumn {
                itemsIndexed(items = items) { index, item ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(item)
                        }
                        .padding(16.dp)
                    ) {
                        Text(text = item)
                    }
                    if (index + 1 < items.size) {
                        Divider(thickness = 1.dp)
                    }
                }
            }
        }
    }
}
