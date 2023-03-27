package com.bird.yy.project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FlashViewModel : ViewModel() {
    var progress: MutableLiveData<Int> = MutableLiveData()
}