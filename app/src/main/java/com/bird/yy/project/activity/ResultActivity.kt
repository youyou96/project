package com.bird.yy.project.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bird.yy.project.base.BaseActivity
import com.bird.yy.project.entity.AdBean
import com.bird.yy.project.entity.CountryBean
import com.bird.yy.project.manager.AdManage
import com.bird.yy.project.utils.Constant
import com.bird.yy.project.utils.EntityUtils
import com.bird.yy.project.utils.SPUtils
import com.google.gson.Gson
import com.unlimited.stable.earth.R
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : BaseActivity() {
    private var img: ImageView? = null
    private var tv: Chronometer? = null
    private var back: ImageView? = null
    private var isStop: Boolean = false
    private var statusTv: TextView? = null
    private var countryTV: TextView? = null
    private var text = ""
    private var resultBackGround: LinearLayout? = null
    private var adManage = AdManage()
    private var frameLayout: FrameLayout? = null

    @SuppressLint("ResourceAsColor", "SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        img = findViewById(R.id.result_country)
        tv = findViewById(R.id.connection_time)
        back = findViewById(R.id.id_back)
        statusTv = findViewById(R.id.connection_status)
        countryTV = findViewById(R.id.country_tv)
        resultBackGround = findViewById(R.id.result_background)
        frameLayout = findViewById(R.id.ad_frameLayout)
        back?.setOnClickListener {
            finish()
        }
        val base = intent.getLongExtra("base", 0)
        isStop = intent.getBooleanExtra("isStop", false)
        text = intent.getStringExtra("text").toString()
        tv?.base = base
        val countryJson = SPUtils.get().getString(Constant.connectedCountryBean, "")
        if (countryJson != null && countryJson.isNotEmpty()) {
            val countryBean = Gson().fromJson(countryJson, CountryBean::class.java)
            if (countryBean != null) {
                val country = EntityUtils().countryBeanToCountry(countryBean)
                countryTV?.text = country.name
                if (country.src == 0) {
                    img?.visibility = View.INVISIBLE
                } else {
                    country.src?.let { img?.setImageResource(it) }
                }
            }
        }

        if (!isStop) {
            tv?.start()
            tv?.setTextColor(resources.getColor(R.color.connection))
            statusTv?.text = "Connected!"
            resultBackGround?.setBackgroundResource(R.drawable.result_connected)
        } else {
            tv?.text = text
            tv?.setTextColor(resources.getColor(R.color.disconnected))
            statusTv?.text = "Disconnected!"
            resultBackGround?.setBackgroundResource(R.drawable.result_disconnected)
            SPUtils.get()
                .putString(Constant.connectedCountryBean, "")
        }
        tv?.setOnChronometerTickListener {
            val time = SystemClock.elapsedRealtime() - it.base
            val date = Date(time)
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            tv?.text = sdf.format(date)
        }
        loadNativeAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        tv?.stop()
    }

    private fun loadNativeAd() {
        val adBean = Constant.AdMap[Constant.adNative_r]
        if (adBean?.ad == null) {
            adManage.loadAd(Constant.adNative_r, this, object : AdManage.OnLoadAdCompleteListener {
                override fun onLoadAdComplete(ad: AdBean?) {
                    if (ad?.ad != null) {
                        adManage.showAd(
                            this@ResultActivity,
                            Constant.adNative_r,
                            ad,
                            frameLayout,
                            object :
                                AdManage.OnShowAdCompleteListener {
                                override fun onShowAdComplete() {
                                }

                                override fun isMax() {
                                }

                            })
                    }
                }

                override fun isMax() {
                }
            })
        } else {
            adManage.showAd(
                this@ResultActivity,
                Constant.adNative_r,
                adBean,
                frameLayout,
                object :
                    AdManage.OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                    }

                    override fun isMax() {
                    }

                })
        }
    }

}