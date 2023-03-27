package com.bird.yy.project.utils

import com.airbnb.lottie.LottieAnimationView

class LottieAnimationViewUtil {
    fun setAnimation(lottieAnimationView: LottieAnimationView) {
        //这个可有可无，如果不涉及本地图片做动画可忽略
        lottieAnimationView.imageAssetsFolder = "images/"
        //设置动画文件
        lottieAnimationView.setAnimation("images/main_lead.json")
        //是否循环执行
        lottieAnimationView.loop(true)
        //执行动画
        lottieAnimationView.playAnimation()
    }
}