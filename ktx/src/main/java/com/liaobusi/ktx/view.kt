package com.liaobusi.ktx


import android.view.View
import android.view.ViewGroup


fun View.detachFromParent() {
    if (this.parent != null) {
        (this.parent as ViewGroup).removeView(this)
    }
}
