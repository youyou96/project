package com.bird.yy.project.base

import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected open fun jumpActivityFinish(clazz: Class<*>?) {
        if (lifecycle.currentState == Lifecycle.State.RESUMED || lifecycle.currentState == Lifecycle.State.STARTED) {
            startActivity(Intent(this, clazz))
            finish()
        }
    }

    protected open fun jumpActivity(clazz: Class<*>?) {
        startActivity(Intent(this, clazz))
    }

    protected open fun showDialogByActivity(
        content: String,
        sure: String,
        cancel: Boolean = true,
        listener: OnClickListener?
    ) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(content)
            .setCancelable(cancel)
            .setPositiveButton(sure, listener)
            .create()
        alertDialog.show()
    }
}