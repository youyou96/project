package com.bird.yy.project.entity

data class AdBean(
    var serpac_id: String,
    var serpac_source: String,
    var serpac_type: String,
    var serpac_pri: Int = 0,
    var ad: Any?=null
)