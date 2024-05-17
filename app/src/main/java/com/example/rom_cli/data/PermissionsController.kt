package com.example.rom_cli.data

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

class PermissionsController {

    companion object {
        val cameraPermissionRequestCode = 200
        val storagePermissionRequestCode = 100

        fun requestPermission(activity: Activity, requestCode: Int, permission: String) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                requestCode
            )
        }
        fun checkPermission(context : Context, permission : String) : Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                false
            }
        }
    }
}