package com.itshedi.jsonparsereditor.jsonStuff.entity

import com.google.gson.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Valore(

    @Expose(serialize = true, deserialize = true)
    @SerializedName("tipo")
    var tipo: String? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("fissato")
    var fissato: String? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("multiplo")
    var multiplo: List<Multiplo>? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("placeholder")
    var placeholder: String? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("titolo")
    var titolo: String? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("valoreString")
    var valoreString: String? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("valoreListString")
    var valoreListString: List<String>? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("valoreListValore")
    var valoreListValore: List<Valore>? = null,

    //don't deserialize thissss
    //this is used to keep the old valore in case the user want to edit*
    @Expose(serialize = false, deserialize = false)
    val ediType:String? = null,

    @Expose(serialize = false, deserialize = false)
    val editList: List<String>? = null,
)

