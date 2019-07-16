package com.liaobusi.ktx

import android.content.Intent
import androidx.fragment.app.Fragment

fun Fragment.startActivityForResult(
    intent: Intent,
    requestCode: Int,
    callback: (resultCode: Int, data: Intent?) -> Unit
) {
    ensureRetainFragment(childFragmentManager).startActivityForResult(intent, requestCode, callback)
}

fun Fragment.ensurePermissions(requestCode: Int, permission: Array<String>, callback: ((grant: Boolean) -> Unit)) {
    ensureRetainFragment(childFragmentManager).ensurePermissions(requestCode, permission, callback)
}

fun Fragment.requestPermissions(
    requestCode: Int,
    permission: Array<String>,
    callback: ((permission: String, grant: Boolean) -> Unit)
) {
    ensureRetainFragment(childFragmentManager).requestPermissions(requestCode, permission, callback)
}
