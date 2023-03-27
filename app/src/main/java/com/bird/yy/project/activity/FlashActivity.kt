package com.bird.yy.project.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bird.yy.project.R
import com.bird.yy.project.base.BaseActivity
import com.bird.yy.project.databinding.ActivityFlashBinding
import com.bird.yy.project.utils.Constant
import com.bird.yy.project.utils.EntityUtils
import com.bird.yy.project.utils.InterNetUtil
import com.bird.yy.project.utils.SPUtils
import com.bird.yy.project.viewmodel.FlashViewModel

private const val COUNTER_TIME = 3L

class FlashActivity : BaseActivity() {
    private var timer: CountDownTimer? = null
    private lateinit var binding: ActivityFlashBinding
    private lateinit var flashViewModel: FlashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_flash)
        flashViewModel = ViewModelProvider(this)[FlashViewModel::class.java]
        countDownTimer()
        flashViewModel.progress.observe(this) {
            binding.viewModel = flashViewModel
        }
        InterNetUtil().getIpByServer(this)
        setData()
    }

    private fun countDownTimer() {
        timer = object : CountDownTimer(COUNTER_TIME * 1000, 1000L) {
            override fun onTick(p0: Long) {
                val process = 100 - (p0 * 100 / COUNTER_TIME / 1000)
                flashViewModel.progress.postValue(process.toInt())
            }

            override fun onFinish() {
                jumpActivityFinish(MainActivity::class.java)
            }

        }
        (timer as CountDownTimer).start()
    }

    override fun onRestart() {
        super.onRestart()
        if (timer != null) {
            timer?.cancel()
            countDownTimer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun setData() {
        if (SPUtils.get().getString(Constant.smart, "")?.isEmpty() == true) {
            val smartJson = EntityUtils().obtainNativeJsonData(this, "city.json")
            SPUtils.get().putString(Constant.smart, smartJson.toString())
            Log.e("smartJson",smartJson.toString())
        }
        if (SPUtils.get().getString(Constant.service, "")?.isEmpty() == true) {
            val serviceJson = EntityUtils().obtainNativeJsonData(this, "service.json")
            SPUtils.get().putString(Constant.service, serviceJson.toString())
            Log.e("serviceJson",serviceJson.toString())
        }
    }
}