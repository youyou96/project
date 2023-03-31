package com.bird.yy.project.entity

import com.google.gson.annotations.SerializedName


data class CountryBean(
    @SerializedName("earth_ip")
    var ip: String = "51.161.131.222",
    @SerializedName("earth_port")
    var port: Int = 4391,
    @SerializedName("earth_pwd")
    var pwd: String = "t7I=X6YounKxhEaz",
    @SerializedName("earth_account")
    var account: String = "chacha20-ietf-poly1305",
    @SerializedName("earth_country")
    var country: String = "Austrilia",
    @SerializedName("earth_city")
    var city: String = "Sydney"

)

