package com.bird.yy.project.entity

import com.google.gson.annotations.SerializedName

data class AdBean(
    @SerializedName("earth_id")
    var serpac_id: String,
    @SerializedName("earth_source")
    var serpac_source: String,
    @SerializedName("earth_type")
    var serpac_type: String,
    @SerializedName("earth_p")
    var serpac_pri: Int = 0,
    var ad: Any?=null
)