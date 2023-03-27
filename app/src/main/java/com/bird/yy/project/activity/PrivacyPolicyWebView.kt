package com.bird.yy.project.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import com.bird.yy.project.R
import com.bird.yy.project.base.BaseActivity
import com.bird.yy.project.utils.Constant

class PrivacyPolicyWebView : BaseActivity() {
    private var webView: WebView? = null
    private var titleName: TextView? = null


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        webView = findViewById(R.id.id_webView)
        titleName = findViewById(R.id.title)
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                titleName?.text = title
            }
        }
        webView?.loadUrl(Constant.PrivacyPolicy)
        val back = findViewById<ImageView>(R.id.id_back)
        back.setOnClickListener {
            finish()
        }
    }
}