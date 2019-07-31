package com.liaobusi.ext

import android.view.View
import com.liaobusi.ktx.Loading
import com.liaobusi.ktx.State
import com.liaobusi.ktx.state

fun View.runWithState(body: View.() -> State) {
    this.state = Loading("loading...")
    this.state = body()
}

fun List<View>.runWithState(body: List<View>.() -> State) {
    this.forEach {
        it.state = Loading("loading...")
    }
    body().let { state ->
        this.forEach { it.state = state }
    }
}

