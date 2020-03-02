package com.fastcampus.vision.api

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtil {

    fun requestPermission(
        activity: Activity, requestCode: Int, vararg permissions: String
    ): Boolean {
        var granted = true
        val permissionNeeded = ArrayList<String>()

        permissions.forEach{
            val permissionCheck = ContextCompat.checkSelfPermission(activity, it)
            val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
            granted = granted and hasPermission
            if(!hasPermission){
                permissionNeeded.add(it)
            }
        }
        if(granted){ return true }
        else {
            ActivityCompat.requestPermissions(
                activity, permissionNeeded.toTypedArray(), requestCode
            )
            return false
        }

    }

    fun permissionGranted(
        requestCode: Int, permissionCode: Int, grantsResults: IntArray
    ): Boolean {
        return requestCode == permissionCode && grantsResults.size > 0 && grantsResults[0] == PackageManager.PERMISSION_GRANTED
    }

}