package com.liaobusi.ktx

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.startActivityForResult(
    intent: Intent,
    requestCode: Int,
    callback: (resultCode: Int, data: Intent?) -> Unit
) {
    ensureRetainFragment(supportFragmentManager).startActivityForResult(intent, requestCode, callback)
}

fun FragmentActivity.ensurePermissions(
    requestCode: Int,
    permission: Array<String>,
    callback: ((grant: Boolean) -> Unit)
) {
    ensureRetainFragment(supportFragmentManager).ensurePermissions(requestCode, permission, callback)
}

fun FragmentActivity.requestPermissions(
    requestCode: Int,
    permission: Array<String>,
    callback: ((permission: String, grant: Boolean) -> Unit)
) {
    ensureRetainFragment(supportFragmentManager).requestPermissions(requestCode, permission, callback)
}


fun Context.getActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext;
    }
    throw  IllegalStateException("The Context is not an Activity.");
}



fun Activity.optionsMenu(resId: Int, menu: Menu?, init: Menu.() -> Unit): Boolean {
    this.menuInflater.inflate(resId, menu)
    menu?.init()
    return (menu?.size ?: 0) > 0
}

fun Menu.onMenuClick(id: Int, callback: (item: MenuItem) -> Boolean) {
    this.findItem(id)?.setOnMenuItemClickListener { callback(it) }
}


