package com.liaobusi.ktx

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class RetainFragment : Fragment() {

    private val requestMap by lazy { mutableMapOf<Int, ((resultCode: Int, data: Intent?) -> Unit)>() }

    private val ensurePermissionMap by lazy { mutableMapOf<Int, ((grant: Boolean) -> Unit)>() }

    private val requestPermissionMap by lazy { mutableMapOf<Int, ((permission: String, grant: Boolean) -> Unit)>() }


    fun ensurePermissions(requestCode: Int, permissions: Array<String>, callback: ((grant: Boolean) -> Unit)) {
        ensurePermissionMap[requestCode] = callback
        requestPermissions(permissions, requestCode)
    }

    fun requestPermissions(
        requestCode: Int,
        permissions: Array<String>,
        callback: (permission: String, grant: Boolean) -> Unit
    ) {
        requestPermissionMap[requestCode] = callback
        requestPermissions(permissions, requestCode)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val ensureExecuteBody = ensurePermissionMap[requestCode]
        if (ensureExecuteBody != null) {
            val grant = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            ensureExecuteBody(grant)
            ensurePermissionMap.remove(requestCode)
            return
        }
        val requestExecuteBody = requestPermissionMap[requestCode]

        if (requestExecuteBody != null) {
            grantResults.forEachIndexed { index, grant ->
                requestExecuteBody(permissions[index], grant == PackageManager.PERMISSION_GRANTED)
            }
            requestPermissionMap.remove(requestCode)
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }


    fun startActivityForResult(
        intent: Intent,
        requestCode: Int,
        callback: ((resultCode: Int, data: Intent?) -> Unit)
    ) {
        requestMap[requestCode] = callback
        this.startActivityForResult(intent, requestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val executeBody = requestMap[requestCode]
        if (executeBody != null) {
            executeBody(resultCode, data)
            requestMap.remove(requestCode)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requestMap.clear()
        ensurePermissionMap.clear()
        requestPermissionMap.clear()
    }


}

private const val TAG = "retain_fragment"

fun ensureRetainFragment(fm: FragmentManager): RetainFragment {
    return fm.run {
        var fragment = findFragmentByTag(TAG)
        if (fragment == null) {
            fragment = RetainFragment()
            beginTransaction().add(fragment, TAG).commitNow()
        }
        fragment as RetainFragment
    }
}

