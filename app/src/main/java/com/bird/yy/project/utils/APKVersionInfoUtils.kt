package com.bird.yy.project.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class APKVersionInfoUtils {
    companion object {
        fun getVerName(context: Context): String? {
            var verName: String? = ""
            try {
                verName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
                }else {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return verName
        }
    }
}