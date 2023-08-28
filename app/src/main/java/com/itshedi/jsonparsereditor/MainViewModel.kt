package com.itshedi.jsonparsereditor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.itshedi.jsonparsereditor.jsonStuff.entity.Multiplo
import com.itshedi.jsonparsereditor.jsonStuff.entity.Valore

class MainViewModel : ViewModel() {


    //deserialized data
    val data = mutableStateListOf<Valore>()

    //NAVIGATED ITEMS !
    //inner data (data,index in parent)
    var currentData = mutableStateListOf<Pair<List<Multiplo>, Int>>()

    // contains the last navigated item if its not null then show summary
    var summarize by mutableStateOf<Pair<Multiplo, Int>?>(null)

    var showFinalResult by mutableStateOf(false)

    //note: this is the final result edited by the user
    var finalResult by mutableStateOf("")

    fun appendFinalResult(): String {
        val result = StringBuilder()

        // note: append the first menu item (including title)
        currentData.firstOrNull()?.second?.let { index ->
            // note: title
            data[0].valoreListValore!![index].titolo?.let { title ->
                result.append(title)
            }

            result.appendLine()

            result.append(
                appendValoreText(
                    items = data[0].valoreListValore!![index].valoreListValore!!
                )
            )
        }
        result.appendLine()

        // note: append the navigated items
        for (i in 0 until currentData.size - 1) {
            currentData[i].first[currentData[i + 1].second].valoreListValore?.let {
                result.append(appendValoreText(items = it))
            }
            result.appendLine()
        }

        // note: append the last navigated item
        summarize?.first?.valoreListValore?.let {
            result.append(appendValoreText(items = it))
        }
        return result.toString()
    }

    // note: this append each valore
    fun appendValoreText(items: List<Valore>): String {
        val sb = StringBuilder()
        items.forEach {
            when (it.tipo) {
                "span" -> {
                    it.valoreString?.let{ vs ->
                        sb.append(vs.replace("<p>", "").replace("</p>", " "))
                    }
                }
                else ->
                    sb.append(
                        when (it.valoreString) {
                            "riferimento" -> ""
                            "int" -> ""
                            "textbox" -> ""
                            "date" -> ""
                            else -> it.valoreString?:"" // this means it's edited
                        }
                    )
            }
            sb.append(" ") // add space after each item
        }
        return sb.toString().replace("\\s+".toRegex(), " ")
    }

}