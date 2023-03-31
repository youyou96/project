package com.bird.yy.project.entity

data class AdResourceBean(
    var serpac_sm: Int,
    var serpac_cm: Int,
    var serpac_o_open: MutableList<AdBean>,
    var serpac_n_home: MutableList<AdBean>,
    var serpac_n_result: MutableList<AdBean>,
    var serpac_i_2R: MutableList<AdBean>,
    var serpac_i_2H: MutableList<AdBean>,
)
