package com.bird.yy.project.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceDataStore
import com.bird.yy.project.R
import com.bird.yy.project.base.BaseActivity
import com.bird.yy.project.databinding.ActivityMainBinding
import com.bird.yy.project.entity.Country
import com.bird.yy.project.entity.CountryBean
import com.bird.yy.project.entity.SmartBean
import com.bird.yy.project.utils.*
import com.bird.yy.project.viewmodel.MainViewModel
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.OnPreferenceDataStoreChangeListener
import com.github.shadowsocks.utils.Key
import com.github.shadowsocks.utils.StartService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity(), ShadowsocksConnection.Callback,
    OnPreferenceDataStoreChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var state = BaseService.State.Idle
    private var text = "00:00"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initView()
        onCLickListener()
        connection.connect(this, this)
        EventBus.getDefault().register(this)
    }

    override fun onRestart() {
        super.onRestart()
        refreshUi()
    }

    private fun onCLickListener() {
        binding.mainSetting.setOnClickListener {
            binding.drawerLayout.toogle()
        }
        binding.mainCardCountry.setOnClickListener {
            //choose service
            val intent = Intent(this, ServiceActivity::class.java)
            intent.putExtra("isConnection", state.canStop)
            startActivity(intent)
        }
        binding.animationView.setOnClickListener {
            connect()
        }
        binding.contactUsLl.setOnClickListener {
            if (binding.drawerLayout.isOpen) {
                openMail()
            }
        }
        binding.updateLl.setOnClickListener {
            if (binding.drawerLayout.isOpen) {
                rateNow()
//                showDialogByActivity("this version is the latest version", "OK", true, null)
            }
        }
        binding.privacyPolicyLl.setOnClickListener {
            if (binding.drawerLayout.isOpen) {
                jumpActivity(PrivacyPolicyWebView::class.java)
            }
        }
        binding.shareLl.setOnClickListener {
            if (binding.drawerLayout.isOpen) {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, Constant.shareUrl)
                intent.type = "text/plain"
                startActivity(intent)
            }
        }
    }

    private fun rateNow() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        intent.setPackage("com.android.vending")
        startActivity(intent)
    }


    private fun openMail() {
        val uri: Uri = Uri.parse("mailto:" + Constant.mail)
        val packageInfos: List<ResolveInfo> =
            packageManager!!.queryIntentActivities(Intent(Intent.ACTION_SENDTO, uri), 0)
        val tempPkgNameList: MutableList<String> = ArrayList()
        val emailIntents: MutableList<Intent> = ArrayList()
        for (info in packageInfos) {
            val pkgName = info.activityInfo.packageName
            if (!tempPkgNameList.contains(pkgName)) {
                tempPkgNameList.add(pkgName)
                val intent: Intent? = packageManager!!.getLaunchIntentForPackage(pkgName)
                if (intent != null) {
                    emailIntents.add(intent)
                }
            }
        }
        if (emailIntents.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            startActivity(intent)
            val chooserIntent =
                Intent.createChooser(intent, "Please select mail application")
            if (chooserIntent != null) {
                startActivity(chooserIntent)
            } else {
                showDialogByActivity("Please set up a Mail account", "OK", true, null)
            }
        } else {
            showDialogByActivity("Please set up a Mail account", "OK", true, null)
        }
    }

    private fun initView() {
        if (!SPUtils.get().getBoolean(Constant.isConnectStatus, false)) {
            val customizedDialog = CustomizedDialog(this, "images/main_lead.json", false, true)
            if (!customizedDialog.isShowing) {
                binding.mainSrcBackground.visibility = View.INVISIBLE
                customizedDialog.show()
            }
            customizedDialog.setOnClick {
                customizedDialog.dismiss()
                binding.mainSrcBackground.visibility = View.VISIBLE
                connect()

            }
            customizedDialog.setOnCancelListener {
                binding.mainSrcBackground.visibility = View.VISIBLE
            }
        }
    }

    private fun connect() {
        if (InterNetUtil().isShowIR()) {
            showDialogByActivity(
                "Due to the policy reason , this service is not available in your country",
                "confirm", false
            ) { dialog, which -> finish() }

        } else {
            isHasNet()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUi() {
        val countryBeanJson = if (state.canStop) SPUtils.get()
            .getString(Constant.connectedCountryBean, "") else SPUtils.get()
            .getString(Constant.connectingCountryBean, "")
        if (countryBeanJson != null) {
            val countryBean = Gson().fromJson(countryBeanJson, CountryBean::class.java)
            if (countryBean != null) {
                val country = EntityUtils().countryBeanToCountry(countryBean)
                country.src?.let { it1 -> binding.mainCountryLogo.setBackgroundResource(it1) }
                binding.mainCountryName.text = country?.name + "-" + country?.city
                Log.e("mainServiceChoose", country?.name + "-" + country?.city)
            }
        }

    }

    private fun toggle() = if (state.canStop) showConnect() else connect.launch(null)
    private fun isHasNet() {
        if (InterNetUtil().isNetConnection(this)) {
            toggle()
        } else {
            showDialogByActivity("Please check your network", "OK", true, null)
        }
    }

    private val connect = registerForActivityResult(StartService()) {
        if (it) Toast.makeText(this, "Missing permissions", Toast.LENGTH_SHORT)
            .show() else showConnect()
    }
    private var countryBean: CountryBean? = null
    private fun connectAnnotation() {
        ProfileManager.clear()
        val countryBeanJson = SPUtils.get().getString(Constant.connectingCountryBean, "")
        if (countryBeanJson != null) {
            if (countryBeanJson.isNotEmpty()) {
                countryBean = Gson().fromJson(countryBeanJson, CountryBean::class.java)
            }
        }
        if (countryBean == null || countryBean?.country?.contains("Super Fast") == true) {
            runBlocking {
                val smartList = getFastSmart()
                if (smartList.isNotEmpty()) {
                    val fast = if (smartList.size >= 3) {
                        Random().nextInt(3)
                    } else {
                        Random().nextInt(smartList.size)
                    }
                    countryBean = smartList[fast].smart
                    countryBean?.country = "Super Fast Server"
                    val country = EntityUtils().countryBeanToCountry(countryBean!!)
                    country.src = R.mipmap.fast
                    val profile = EntityUtils().countryToProfile(country)
                    val profileNew = ProfileManager.createProfile(profile)
                    Core.switchProfile(profileNew.id)
                    SPUtils.get()
                        .putString(Constant.connectingCountryBean, Gson().toJson(countryBean))
                }
            }
        } else {
            val country = countryBean?.let { EntityUtils().countryBeanToCountry(it) }
            val profile = country?.let { EntityUtils().countryToProfile(it) }
            val profileNew = profile?.let { ProfileManager.createProfile(it) }
            profileNew?.id?.let { Core.switchProfile(it) }
            SPUtils.get().putString(Constant.connectingCountryBean, Gson().toJson(countryBean))
        }
    }

    private suspend fun getFastSmart(): MutableList<SmartBean> {
        val smartJson = SPUtils.get().getString(Constant.smart, "")
        val serviceJson = SPUtils.get().getString(Constant.service, "")
        var serviceList: MutableList<CountryBean> = mutableListOf()
        val smartBeanList: MutableList<SmartBean> = mutableListOf()
        if (serviceJson?.isNotEmpty() == true) {
            val serviceType: Type = object : TypeToken<List<CountryBean?>?>() {}.type
            serviceList = Gson().fromJson(serviceJson, serviceType)
        }
        if (smartJson?.isNotEmpty() == true) {
            val type: Type = object : TypeToken<List<String?>?>() {}.type
            val smartList: MutableList<String> = Gson().fromJson(smartJson, type)
            if (smartList.isNotEmpty() && serviceList.isNotEmpty()) {
                for (item in smartList) {
                    for (service in serviceList) {
                        if (item == service.city) {
                            smartBeanList.add(
                                SmartBean(
                                    service,
                                    InterNetUtil().delayTest(service.ip, 1)
                                )
                            )
                        }
                    }

                }
            }
        }
        return smartBeanList
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        changeConnectionStatus(state)
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        changeConnectionStatus(
            try {
                BaseService.State.values()[service.state]
            } catch (_: RemoteException) {
                BaseService.State.Idle
            }
        )
    }

    private val connection = ShadowsocksConnection(true)
    override fun onBinderDied() {
        connection.disconnect(this)
        connection.connect(this, this)
    }

    override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {
        when (key) {
            Key.serviceMode -> {
                connection.disconnect(this)
                connection.connect(this, this)
            }
        }
    }

    private fun changeConnectionStatus(status: BaseService.State) {
        this.state = status
        when (status) {
            BaseService.State.Idle -> {
                SPUtils.get().putBoolean(Constant.isConnectStatus, false)
                binding.animationView.setBackgroundResource(R.mipmap.main_disconnected)
                binding.mainStatusImg.setBackgroundResource(R.mipmap.main_status_background)
                binding.mainStatusImg2.setBackgroundResource(R.mipmap.main_status)
                binding.theConnectionTimeTv.stop()
                binding.theConnectionTimeTv.setTextColor(getColor(R.color.main_connect_time))
                binding.theConnectionTimeTv.text = "00:00:00"
                Toast.makeText(this, "please try again", Toast.LENGTH_LONG).show()
            }
            BaseService.State.Connected -> {
                SPUtils.get().putBoolean(Constant.isConnectStatus, true)
                SPUtils.get().putString(Constant.connectedCountryBean, Gson().toJson(countryBean))
                binding.animationView.setBackgroundResource(R.mipmap.main_connected)
                binding.mainStatusImg.setBackgroundResource(R.mipmap.main_status_connected_background)
                binding.mainStatusImg2.setBackgroundResource(R.mipmap.main_status_connected)
                binding.theConnectionTimeTv.setTextColor(getColor(R.color.connection))
                binding.theConnectionTimeTv.setOnChronometerTickListener {
                    val time = SystemClock.elapsedRealtime() - it.base
                    val date = Date(time)
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    binding.theConnectionTimeTv.text = sdf.format(date)
                }
                val connectTime = SPUtils.get().getLong(Constant.connectTime, 0)
                if (connectTime > 0) {
                    binding.theConnectionTimeTv.base = connectTime
                } else {
                    binding.theConnectionTimeTv.base = SystemClock.elapsedRealtime()
                }
                if (SystemClock.elapsedRealtime() - (binding.theConnectionTimeTv.base) < 20 && SPUtils.get()
                        .getBoolean(Constant.isShowResultKey, false)
                ) {
                    SPUtils.get().putBoolean(Constant.isShowResultKey, false)
                    lifecycleScope.launch(Dispatchers.Main.immediate) {
                        delay(300L)
                        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                            var country: Country? = null
                            if (countryBean != null) {
                                country = EntityUtils().countryBeanToCountry(countryBean!!)
                            }
                            val srcInt = if (country != null) country!!.src else R.mipmap.fast
                            val intent = Intent(this@MainActivity, ResultActivity::class.java)
                            intent.putExtra("base", binding.theConnectionTimeTv.base)
                            intent.putExtra("srcInt", srcInt)
                            startActivity(intent)
                        }
                    }
                }
                binding.theConnectionTimeTv.start()
            }
            BaseService.State.Stopped -> {
                binding.animationView.setBackgroundResource(R.mipmap.main_disconnected)
                binding.mainStatusImg.setBackgroundResource(R.mipmap.main_status_background)
                binding.mainStatusImg2.setBackgroundResource(R.mipmap.main_status)
                binding.theConnectionTimeTv.stop()
                binding.theConnectionTimeTv.text = "00:00:00"
                binding.theConnectionTimeTv.setTextColor(getColor(R.color.main_connect_time))
                SPUtils.get().putLong(Constant.connectTime, 0L)
                if (SPUtils.get()
                        .getBoolean(Constant.isShowResultKey, false) && text != "00:00:00"
                ) {
                    SPUtils.get().putBoolean(Constant.isConnectStatus, false)
                    var country: Country? = null
                    if (countryBean != null) {
                        country = EntityUtils().countryBeanToCountry(countryBean!!)
                    }
                    val srcInt = if (country != null) country!!.src else R.mipmap.fast
                    SPUtils.get().putBoolean(Constant.isShowResultKey, false)
                    lifecycleScope.launch(Dispatchers.Main.immediate) {
                        delay(300L)
                        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                            val intent = Intent(this@MainActivity, ResultActivity::class.java)
                            intent.putExtra("text", text)
                            intent.putExtra("isStop", true)
                            intent.putExtra("srcInt", srcInt)
                            startActivity(intent)
                        }
                    }

                }
            }
            else -> {
                SPUtils.get().putBoolean(Constant.isConnectStatus, false)
                binding.animationView.setBackgroundResource(R.mipmap.main_disconnected)
                binding.mainStatusImg.setBackgroundResource(R.mipmap.main_status_background)
                binding.mainStatusImg2.setBackgroundResource(R.mipmap.main_status)
                binding.theConnectionTimeTv.base = SystemClock.elapsedRealtime()
                binding.theConnectionTimeTv.stop()
                binding.theConnectionTimeTv.text = "00:00:00"
                binding.theConnectionTimeTv.setTextColor(getColor(R.color.main_connect_time))
                SPUtils.get().putLong(Constant.connectTime, 0L)
            }
        }
    }

    private var connectionJob: Job? = null
    private fun showConnect() {
        if (state.canStop) {
            text = binding.theConnectionTimeTv.text as String
        }
        SPUtils.get().putBoolean(Constant.isShowResultKey, true)
        val isCancel = state.canStop
        val customizedDialog =
            CustomizedDialog(this@MainActivity, "images/data.json", isCancel, isCancel)
        connectionJob = lifecycleScope.launch {
            flow {
                (0 until 3).forEach {
                    delay(1000)
                    emit(it)
                }
            }.onStart {
                //start
                customizedDialog.show()
                connectAnnotation()
            }.onCompletion {
                //finish
                customizedDialog.dismiss()
                if (state.canStop) {
                    Core.stopService()
                } else {
                    Core.startService()
                }
                mainViewModel.country.postValue(countryBean)
            }.collect {
                //process
                if (!customizedDialog.isShowing) {
                    connectionJob?.cancel()
                    return@collect
                }

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Country?) {
        connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        SPUtils.get().putLong(Constant.connectTime, binding.theConnectionTimeTv.base)
        SPUtils.get().putBoolean(Constant.isShowResultKey, false)
        SPUtils.get().putString(Constant.connectedCountryBean, "")
        SPUtils.get().putString(Constant.connectingCountryBean, "")
        EventBus.getDefault().unregister(this)
    }
}
