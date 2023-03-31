package com.bird.yy.project.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bird.yy.project.base.BaseActivity
import com.bird.yy.project.entity.CountryBean
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
    }

    override fun onDestroy() {
        super.onDestroy()
        tv?.stop()
    }
}