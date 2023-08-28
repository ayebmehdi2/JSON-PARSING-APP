package com.itshedi.jsonparsereditor.jsonStuff.entity


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Multiplo(
    @Expose(serialize = true, deserialize = true)
    @SerializedName("fissato")
    var fissato: String? = null,
    
    @Expose(serialize = true, deserialize = true)
    @SerializedName("multiplo")
    var multiplo: List<Multiplo>? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("valoreString")
    var valoreString: String? = null,
    
    @Expose(serialize = true, deserialize = true)
    @SerializedName("valoreListString")
    var valoreListString: List<String>? = null,

    @Expose(serialize = true, deserialize = true)
    @SerializedName("valoreListValore")
    var valoreListValore: List<Valore>? = null,
)