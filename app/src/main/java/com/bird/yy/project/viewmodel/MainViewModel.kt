package com.bird.yy.project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bird.yy.project.entity.CountryBean

class MainViewModel : ViewModel() {
     var country: MutableLiveData<CountryBean> = MutableLiveData()

}