package com.itshedi.jsonparsereditor.jsonStuff

import java.util.regex.Pattern


//note: use this to map each data type in "valore" to its data type

fun transformJson(json: String): String {
    return json.replace(
        regex = Pattern.compile(""""valore"\s*:\s*\[\n*\t*\s*\"""").toRegex(),
    ){ _ -> "\"valoreListString\": [\""}
        .replace(
            regex = Pattern.compile(""""valore"\s*:\s*\[\n*\t*\s*\{""").toRegex(),
        ){ _ -> "\"valoreListValore\": [{"}
        .replace(
            regex = Pattern.compile(""""valore"\s*:\n*\t*\s*"""").toRegex(),
        ){ _ -> "\"valoreString\": \""}

}

fun revertJsonToNormal(json: String): String {
    return json.replace(
        regex = Pattern.compile(""""valoreListString"\s*:\s*\[\n*\t*\s*\"""").toRegex(),
        "\"valore\": [\""
    )
        .replace(
            regex = Pattern.compile(""""valoreListValore"\s*:\s*\[\n*\t*\s*\{""").toRegex(),
            "\"valore\": [{"
        )
        .replace(
            regex = Pattern.compile(""""valoreString"\s*:\n*\t*\s*"""").toRegex(),
            "\"valore\": \""
        )
}