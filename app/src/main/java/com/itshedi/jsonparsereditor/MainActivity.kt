package com.itshedi.jsonparsereditor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.flowlayout.FlowRow
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.itshedi.jsonparsereditor.jsonStuff.FilePicker
import com.itshedi.jsonparsereditor.jsonStuff.entity.Multiplo
import com.itshedi.jsonparsereditor.jsonStuff.entity.Valore
import com.itshedi.jsonparsereditor.jsonStuff.revertJsonToNormal
import com.itshedi.jsonparsereditor.jsonStuff.transformJson
import com.itshedi.jsonparsereditor.ui.DatePicker
import com.itshedi.jsonparsereditor.ui.InputDialog
import com.itshedi.jsonparsereditor.ui.ListPickerDialog
import com.itshedi.jsonparsereditor.ui.theme.JSONParserEditorTheme
import com.itshedi.jsonparsereditor.ui.theme.accentColor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class MainActivity : ComponentActivity() {

    val TAG = "COOLTAG"

    lateinit var viewmodel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {


            BackHandler(enabled = viewmodel.currentData.isNotEmpty() || viewmodel.summarize != null || viewmodel.data.isNotEmpty() || viewmodel.showFinalResult) {
                if (viewmodel.showFinalResult){
                    viewmodel.showFinalResult = false
                }else if (viewmodel.summarize != null) {
                    viewmodel.summarize = null
                } else {
                    if (viewmodel.currentData.isEmpty()) {
                        viewmodel.data.clear()
                    } else {
                        viewmodel.currentData.removeLast()
                    }
                }
            }

            JSONParserEditorTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (viewmodel.data.isEmpty()) {
                            true -> {
                                FilePicker(onFileContent = { content ->
                                    try {
                                        val itemType = object : TypeToken<List<Valore>>() {}.type
                                        viewmodel.data.addAll(
                                            Gson().fromJson<ArrayList<Valore>>(
                                                transformJson(
                                                    content
                                                ), itemType
                                            )
                                        )
                                    } catch (e: Exception) {
                                        Log.i(TAG, e.message.toString())
                                    }

                                })
                            }
                            false -> {
                                MainLayout(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp),
                                    onUpdated = {
                                        viewmodel.data.clear()
                                        viewmodel.data.addAll(it)
                                    },
                                    onAvanti = {
                                        viewmodel.showFinalResult = true
                                        val result = revertJsonToNormal(
                                            GsonBuilder().disableHtmlEscaping()
                                                .excludeFieldsWithoutExposeAnnotation()
                                                .setPrettyPrinting().create()
                                                .toJson(viewmodel.data)
                                        )
                                        Log.i(TAG, result)
                                        writeResultToFile(result)
                                    })
                            }
                        }
                    }
                }
            }
        }
    }


    fun writeResultToFile(result: String) {
        try {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            val s = (1..10)
                .map { allowedChars.random() }
                .joinToString("")

            //note: this is for external
//            val file = File(
//                Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOCUMENTS
//                ),
//                "result_$s.json"
//            )
            val outputWriter = OutputStreamWriter( openFileOutput("result_$s.json", Context.MODE_PRIVATE))
            outputWriter.write(result)
            outputWriter.close()
            //display file saved message
            Log.i(TAG, "file saved result_$s.json")
        } catch (e: Exception) {
            Log.i(TAG, e.message.toString())
            e.printStackTrace()
        }
    }

    @Composable
    fun MainLayout(modifier: Modifier, onUpdated: (List<Valore>) -> Unit, onAvanti: () -> Unit) {
        Column(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(text = "Verbale", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Compila i campi",
                        color = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium)
                    )
                }
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier
                        .clickable {
                            if (viewmodel.showFinalResult) {
                                viewmodel.showFinalResult = false
                            } else if (viewmodel.summarize != null) {
                                viewmodel.summarize = null
                            } else {
                                if (viewmodel.currentData.isEmpty()) {
                                    viewmodel.data.clear()
                                } else {
                                    viewmodel.currentData.removeLast()
                                }
                            }
                        }
                        .padding(16.dp))
            }

            ListContainer(data = viewmodel.data,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onUpdated = {
                    onUpdated(it)
                },
                onInnerDataUpdated = { list, depth, index ->
                    viewmodel.currentData[depth] = Pair(list, viewmodel.currentData[depth].second)
                    onUpdated(copyCurrentChildrenToRoot(depth = depth))
                })

            if(!viewmodel.showFinalResult){
                Button(
                    onClick = {
                        onAvanti()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .height(60.dp),
                    enabled = viewmodel.summarize != null && !viewmodel.showFinalResult
                ) {
                    Text("AVANTI")
                }
            }

        }
    }


    //note: Root of the list
    @Composable
    fun ListContainer(
        data: List<Valore>,
        modifier: Modifier,
        onUpdated: (List<Valore>) -> Unit,
        onInnerDataUpdated: (List<Multiplo>, Int, Int) -> Unit
    ) { // depth 0
        if(!viewmodel.showFinalResult){
            if (viewmodel.summarize == null) {
                if (viewmodel.currentData.isEmpty()) {
                    LazyColumn(modifier = modifier) { // main menu
                        //note: this is the root item data[0] with tipo="combo"
                        if (data.isNotEmpty()) {
                            data[0].valoreListValore?.let { mainItems ->
                                itemsIndexed(mainItems) { index, item -> // depth 2
                                    MainItem(item = item, hasItems = item.multiplo != null, onClick = {
                                        item.multiplo?.let {
                                            viewmodel.currentData.add(Pair(it, index))
                                        }
                                    }, onUpdated = { valore -> // main item with title
                                        val mainValoreList = ArrayList<Valore>(mainItems)
                                        mainValoreList[index] = valore

                                        val newValoreList = ArrayList<Valore>(data)
                                        newValoreList[0] =
                                            data[0].copy(valoreListValore = mainValoreList)

                                        onUpdated(newValoreList)
                                    })
                                }
                            }
                        }
                    }
                } else {
                    viewmodel.currentData.lastOrNull()?.let { (lastList, _) ->
                        LazyColumn(modifier = modifier) { // multiple
                            itemsIndexed(lastList) { index, item -> //depth 2 + currentData.size - 1
                                MultiploItem(item = item, hasItems = item.multiplo != null, onClick = {
                                    if (item.multiplo != null) {
                                        item.multiplo?.let {
                                            viewmodel.currentData.add(Pair(it, index))
                                        }
                                    } else {
                                        viewmodel.summarize = Pair(item, index)
                                    }
                                }, onUpdated = {
                                    val newMultiploList = ArrayList<Multiplo>(lastList)
                                    newMultiploList[index] = it
                                    onInnerDataUpdated(
                                        newMultiploList,
                                        viewmodel.currentData.size - 1,
                                        index
                                    )
                                })
                            }

                        }
                    }
                }
            } else {
                Summary(modifier = modifier, onUpdated = { it, depth, index ->
                    val d =
                        if (depth > viewmodel.currentData.size - 1) viewmodel.currentData.size - 1 else depth

                    val newMultiplo = ArrayList<Multiplo>(viewmodel.currentData[d].first)
                    newMultiplo[index] = it
                    viewmodel.currentData[d] = viewmodel.currentData[d].copy(
                        first = newMultiplo
                    )
                    onUpdated(copyCurrentChildrenToRoot(depth = d))
                }, onRootUpdated = { newValoreList, index ->
                    data[0].valoreListValore?.let { mainItems ->
                        val mainValoreList = ArrayList<Valore>(mainItems)
                        mainValoreList[index] =
                            mainValoreList[index].copy(valoreListValore = newValoreList)
                        val newData = ArrayList<Valore>(data)
                        newData[0] = data[0].copy(valoreListValore = mainValoreList)

                        onUpdated(newData)
                    }
                })
            }
        } else {
            FinalResult(modifier = modifier)
        }
    }

    private fun copyCurrentChildrenToRoot(depth: Int): List<Valore> {
        //updating currentdata
        var d = depth - 1
        while (d >= 0) {
            val nextItem = viewmodel.currentData[d + 1]
            val newMultiploList = ArrayList<Multiplo>(viewmodel.currentData[d].first)
            newMultiploList[nextItem.second].multiplo = nextItem.first
            viewmodel.currentData[d] = Pair(newMultiploList, viewmodel.currentData[d].second)
            d--
        }

        //setting current data in root
        val newData = ArrayList<Valore>(viewmodel.data) // the whole file
        val listContainers = ArrayList<Valore>(newData[0].valoreListValore!!) // container list
        val indexOfMainContainer = viewmodel.currentData[0].second
        listContainers[indexOfMainContainer] =
            listContainers[indexOfMainContainer].copy(multiplo = viewmodel.currentData[0].first) // container
        newData[0] = newData[0].copy(valoreListValore = listContainers)
        return newData
    }

    //note: final result screen without yellow hightlight
    @Composable
    fun FinalResult(modifier: Modifier) {
        LaunchedEffect(true){
            viewmodel.finalResult = viewmodel.appendFinalResult()
        }
        Column(
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Compilazione finale",
                color = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp), elevation = 4.dp,
                contentColor = Color.White
            ) {
                Column(modifier = modifier.padding(20.dp)) {
                    BasicTextField(value = viewmodel.finalResult, onValueChange = {
                        viewmodel.finalResult = it
                    }, textStyle = TextStyle(color = MaterialTheme.colors.onBackground, fontSize = 16.sp),
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    )
                }
            }
        }
    }

    @Composable
    fun Summary(
        modifier: Modifier,
        onUpdated: (Multiplo, Int, Int) -> Unit,
        onRootUpdated: (List<Valore>, Int) -> Unit
    ) {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Compilazione assistita",
                color = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp), elevation = 4.dp
            ) {
                Column(modifier = modifier.padding(20.dp)) {
                    viewmodel.currentData.firstOrNull()?.second?.let { index ->
                        viewmodel.data[0].valoreListValore!![index].titolo?.let { title ->
                            Text(title, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        ValoreGroupBody(
                            items = viewmodel.data[0].valoreListValore!![index].valoreListValore!!,
                            onUpdated = { newListValore ->
                                onRootUpdated(newListValore, index)
                            })
                    }
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    for (i in 0 until viewmodel.currentData.size - 1) {
                        viewmodel.currentData[i].first[viewmodel.currentData[i + 1].second].valoreListValore?.let {
                            ValoreGroupBody(items = it, onUpdated = { newListValore ->
                                onUpdated(
                                    viewmodel.currentData[i].first[viewmodel.currentData[i + 1].second].copy(
                                        valoreListValore = newListValore
                                    ), i, viewmodel.currentData[i + 1].second
                                )
                            })
                            Spacer(modifier = Modifier.padding(top = 10.dp))
                        }
                    }
                    viewmodel.summarize?.first?.valoreListValore?.let {
                        ValoreGroupBody(items = it, onUpdated = { newListValore ->
                            viewmodel.summarize = viewmodel.summarize!!.copy(
                                first = viewmodel.summarize!!.first.copy(valoreListValore = newListValore)
                            )
                            onUpdated(
                                viewmodel.summarize!!.first,
                                viewmodel.currentData.size + 1,
                                viewmodel.summarize!!.second
                            )
                        })
                    }
                }
            }
        }
    }

    //note: main menu items ( with title )
    @Composable
    fun MultiploItem(
        item: Multiplo,
        hasItems: Boolean,
        onClick: (Multiplo) -> Unit,
        onUpdated: (Multiplo) -> Unit
    ) {
        Card(
            Modifier.padding(16.dp), elevation = 4.dp
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(item) }
                .padding(10.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    item.valoreListValore?.let {
                        ValoreGroupBody(items = it, onUpdated = { newListValore ->
                            onUpdated(item.copy(valoreListValore = newListValore))
                        })
                    }
                }
                if (hasItems) {
                    Altro()
                }
            }
        }

    }

    //note: alro tag
    @Composable
    fun Altro() {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colors.onSurface.copy(0.6f)
                )
                Spacer(modifier = Modifier.padding(start = 2.dp))
                Text(
                    text = "Altro",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface.copy(0.6f)
                )
            }
        }
    }

    //note: card containg main item
    @Composable
    fun MainItem(
        item: Valore, hasItems: Boolean, onClick: () -> Unit, onUpdated: (Valore) -> Unit
    ) {
        Card(
            Modifier.padding(16.dp), elevation = 4.dp
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(10.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    item.titolo?.let {
                        Text(it, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    item.valoreListValore?.let {
                        ValoreGroupBody(items = it, onUpdated = { valoreList ->
                            onUpdated(item.copy(valoreListValore = valoreList))
                        })
                    }
                }
                if (hasItems) {
                    Altro()
                }
            }
        }
    }


    //note: Body of the card
    @Composable
    fun ValoreGroupBody(items: List<Valore>, onUpdated: (List<Valore>) -> Unit) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, it ->
                when (it.tipo) {
                    "span" -> {
                        TextToFlowText(text = it.valoreString.toString())
                    }
                    else -> when (it.valoreString) {
                        "riferimento" -> { /* TODO */
                        }
                        else -> InsertionButton(
                            editType = it.ediType,
                            text = it.valoreString.toString(),
                            type = it.tipo, //if it has editType then it was edited before
                            onUpdated = { valore ->
                                val newItems = ArrayList<Valore>(items)
                                newItems[index] = it.copy(
                                    valoreListValore = null,
                                    valoreListString = null,
                                    editList = it.editList ?: it.valoreListString,
                                    ediType = it.ediType ?: when (it.valoreListString) {
                                        null -> it.valoreString
                                        else -> it.tipo
                                    }, // keep the type (use it to edit)
                                    valoreString = valore,
                                )
                                onUpdated(
                                    newItems
                                )
                            },
                            listItems = it.valoreListString,
                            editList = it.editList
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun TextToFlowText(text: String) {
        text.replace("<p>", "").replace("</p>", "")
            .trim().split(" ")
            .forEach { word ->
                if(word.trim().isNotBlank()){
                    Text("$word ", modifier = Modifier.padding(horizontal = 2.dp))
                }
            }
    }
    //note: represents a signle yellow element that should be filled by user
    @Composable
    fun InsertionButton(
        editType: String?,
        editList: List<String>?,
        text: String?,
        type: String?,
        onUpdated: (String) -> Unit,
        listItems: List<String>? = null
    ) {
        var showDatePicker by remember { mutableStateOf(false) }
        var showValueInputDialog by remember { mutableStateOf(false) }
        var showListPickerDialog by remember { mutableStateOf(false) }
        var inputType by remember { mutableStateOf(0) }

        if (showDatePicker) {
            DatePicker(onDateSelected = { onUpdated(it.toString()) },
                onDismissRequest = { showDatePicker = false })
        }
        if (showValueInputDialog) {
            InputDialog(type = inputType,
                onConfirm = { onUpdated(it) },
                onDismissRequest = { showValueInputDialog = false })
        }
        (editList ?: listItems)?.let { items ->
            if (showListPickerDialog) {
                ListPickerDialog(items = items, onSelect = {
                    showListPickerDialog = false
                    onUpdated(it)
                }, onDismissRequest = { showListPickerDialog = false })
            }
        }

        Box(modifier = Modifier
            .padding(horizontal = 2.dp)
            .background(Color.Yellow)
            .clickable {
                if ((editType ?: type) == "lista") {
                    showListPickerDialog = true
                } else {
                    when (editType ?: text) {
                        "int" -> {
                            inputType = 1
                            showValueInputDialog = true
                        }
                        "textbox" -> {
                            inputType = 0
                            showValueInputDialog = true
                        }
                        "date" -> {
                            showDatePicker = true
                        }
                    }
                }
            }
            .padding(horizontal = 2.dp)) {
            Text(
                when (type) {
                    "lista" -> when (listItems) {
                        null -> text ?: "" // this means it's edited
                        else -> "Seleziona valore"
                    }
                    else -> when (text) {
                        "int" -> "Inserisci valore"
                        "textbox" -> "Inserisci testo"
                        "date" -> "Inserisci data"
                        else -> text ?: "" // this means it's edited
                    }
                }, color = Color.Black
            )
        }
    }
}


