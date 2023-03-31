package com.bird.yy.project.utils

import android.content.Context
import com.bird.yy.project.entity.Country
import com.bird.yy.project.entity.CountryBean
import com.github.shadowsocks.database.Profile
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.unlimited.stable.earth.R
import java.io.BufferedReader
import java.io.InputStreamReader

class EntityUtils {
    fun countryToProfile(country: Country): Profile {
        val profile = Profile()
        profile.name = country.name
        profile.host = country.host
        profile.method = country.account
        profile.password = country.password
        profile.remotePort = country.remotePort

        return profile
    }

    fun obtainNativeJsonData(context: Context, jsonDataName: String): StringBuilder {
        val assetManager = context.assets
        val inputStreamReader = InputStreamReader(assetManager.open(jsonDataName), "UTF-8")
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder = StringBuilder()
        val iterator = bufferedReader.lineSequence().iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            stringBuilder.append(line)
        }
        bufferedReader.close()
        inputStreamReader.close()
        return stringBuilder
    }

    fun countryBeanToCountry(countryBean: CountryBean): Country {
        val country = Country()
        country.name = countryBean.country
        country.city = countryBean.city
        country.host = countryBean.ip
        country.password = countryBean.pwd
        country.method = countryBean.account
        country.remotePort = countryBean.port
        if (countryBean.country.contains("tates")) {
            country.src = R.mipmap.unitedstates
        } else if (countryBean.country.contains("ndia")) {
            country.src = R.mipmap.india
        } else if (countryBean.country.contains("reland")) {
            country.src = R.mipmap.ireland
        } else if (countryBean.country.contains("srael")) {
            country.src = R.mipmap.israel
        } else if (countryBean.country.contains("taly")) {
            country.src = R.mipmap.italy
        } else if (countryBean.country.contains("apan")) {
            country.src = R.mipmap.japan
        } else if (countryBean.country.contains("orea")) {
            country.src = R.mipmap.koreasouth
        } else if (countryBean.country.contains("etherlands")) {
            country.src = R.mipmap.netherlands
        } else if (countryBean.country.contains("ealand")) {
            country.src = R.mipmap.newzealand
        } else if (countryBean.country.contains("orway")) {
            country.src = R.mipmap.norway
        } else if (countryBean.country.contains("ingapore")) {
            country.src = R.mipmap.singapore
        } else if (countryBean.country.contains("weden")) {
            country.src = R.mipmap.sweden
        } else if (countryBean.country.contains("witzerland")) {
            country.src = R.mipmap.switzerland
        } else if (countryBean.country.contains("aiwan")) {
            country.src = R.mipmap.taiwan
        } else if (countryBean.country.contains("urkey")) {
            country.src = R.mipmap.turkey
        } else if (countryBean.country.contains("weden")) {
            country.src = R.mipmap.sweden
        } else if (countryBean.country.contains("mirates")) {
            country.src = R.mipmap.unitedarabemirates
        } else if (countryBean.country.contains("ingdom")) {
            country.src = R.mipmap.unitedkingdom
        } else if (countryBean.country.contains("ustrilia")) {
            country.src = R.mipmap.australia
        } else if (countryBean.country.contains("elgium")) {
            country.src = R.mipmap.belgium
        } else if (countryBean.country.contains("razil")) {
            country.src = R.mipmap.brazil
        } else if (countryBean.country.contains("anada")) {
            country.src = R.mipmap.canada
        } else if (countryBean.country.contains("rance")) {
            country.src = R.mipmap.france
        } else if (countryBean.country.contains("ermany")) {
            country.src = R.mipmap.germany
        } else if (countryBean.country.contains("ong")) {
            country.src = R.mipmap.hongkong
        }else {
            country.src = R.mipmap.fast
        }
        return country
    }
    fun getServiceData(context: Context){

        try {
            val remoteConfig = Firebase.remoteConfig
            remoteConfig.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful) {
                    val serviceVpnData = remoteConfig.getString("earth_servlist")
                    if (serviceVpnData.isNotEmpty()){
                        SPUtils.get().putString(Constant.service,serviceVpnData)
                    }else{
                        val serviceJson = EntityUtils().obtainNativeJsonData(context, "service.json")
                        SPUtils.get().putString(Constant.service, serviceJson.toString())
                    }

                    val smartServiceVpnData = remoteConfig.getString("earth_smart")
                    if (smartServiceVpnData.isNotEmpty()){
                        SPUtils.get().putString(Constant.smart, smartServiceVpnData)

                    }else{
                        val serviceJson = EntityUtils().obtainNativeJsonData(context, "city.json")
                        SPUtils.get().putString(Constant.smart, serviceJson.toString())
                    }
                    val adData = remoteConfig.getString("earth_ad")
                    if (adData.isNotEmpty()){
                        SPUtils.get().putString(Constant.adResourceBean, adData)
                    }else{
                        val adResourceBeanJson = EntityUtils().obtainNativeJsonData(context, "ad.json").toString()
                        SPUtils.get().putString(Constant.adResourceBean, adResourceBeanJson)
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

