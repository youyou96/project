package com.bird.yy.project.entity

import com.google.gson.annotations.SerializedName

data class AdResourceBean(
    @SerializedName("day_show_limit")
    var serpac_sm: Int,
    @SerializedName("day_click_limit")
    var serpac_cm: Int,
    @SerializedName("o_open")
    var serpac_o_open: MutableList<AdBean>,
    @SerializedName("n_ho")
    var serpac_n_home: MutableList<AdBean>,
    @SerializedName("n_down")
    var serpac_n_result: MutableList<AdBean>,
    @SerializedName("i_go")
    var serpac_i_2R: MutableList<AdBean>,
    @SerializedName("i_black")
    var serpac_i_2H: MutableList<AdBean>,
)
