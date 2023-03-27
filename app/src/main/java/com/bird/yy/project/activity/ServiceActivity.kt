package com.bird.yy.project.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bird.yy.project.R
import com.bird.yy.project.adapter.LocationAdapter
import com.bird.yy.project.base.BaseActivity
import com.bird.yy.project.entity.Country
import com.bird.yy.project.entity.CountryBean
import com.bird.yy.project.utils.Constant
import com.bird.yy.project.utils.EntityUtils
import com.bird.yy.project.utils.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Type

class ServiceActivity : BaseActivity() {
    private var recyclerView: RecyclerView? = null
    private var locationAdapter: LocationAdapter? = null
    private val layoutManager by lazy { LinearLayoutManager(this, RecyclerView.VERTICAL, false) }
    private var isConnection = false
    private var arrBack:ImageView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_country)
        recyclerView = findViewById(R.id.id_recyclerview)
        locationAdapter = LocationAdapter()
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = locationAdapter
        isConnection = intent.getBooleanExtra("isConnection", false)
        arrBack = findViewById(R.id.id_back)
        arrBack?.setOnClickListener {
            finish()
        }
        setList()
        locationAdapter?.setOnItemClickListener(object : LocationAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, country: Country) {
                SPUtils.get().putString(Constant.chooseCountry, Gson().toJson(country))
                if (country.isChoose == true) {
                    finish()
                } else {
                    if (isConnection) {
                        val alertDialog = AlertDialog.Builder(this@ServiceActivity)
                            .setMessage("If you want to connect to another VPN, you need to disconnect the current connection first. Do you want to disconnect the current connection?")
                            .setPositiveButton("yes") { p0, p1 ->
                                EventBus.getDefault().post(country)
                                finish()
                            }
                            .setNegativeButton("no", null)
                            .create()
                        alertDialog.show()
                    } else {
                        EventBus.getDefault().post(country)
                        finish()
                    }

                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setList() {
        val countryJson: String? = SPUtils.get().getString(Constant.service, "")
        val countryList: MutableList<Country> = ArrayList()
        countryList.add(Country(0, "Fast Smart Server"))
        if (countryJson != null) {
            if (countryJson.isNotEmpty()) {
                val type: Type = object : TypeToken<List<CountryBean?>?>() {}.type
                val countryBean: MutableList<CountryBean> =
                    Gson().fromJson(countryJson.toString(), type)
                if (countryBean.isNotEmpty()) {
                    countryBean.forEach {
                        val country: Country = EntityUtils().countryBeanToCountry(it)
                        countryList.add(country)
                    }
                }
            }
        }
        val countryString = SPUtils.get().getString(Constant.chooseCountry, "")
        if (countryString != null && countryString.isNotEmpty()) {
            val country = Gson().fromJson(countryString, Country::class.java)
            if (country != null) {
                val profileName = country.name
                for (item in countryList) {
                    if (profileName?.contains(item.name!!) == true) {
                        item.isChoose = true
                    }
                }
            }
        }

        locationAdapter?.setList(countryList)
        locationAdapter?.notifyDataSetChanged()
    }
}