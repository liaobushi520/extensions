package com.liaobusi.ktx

import android.view.View
import android.widget.FrameLayout


sealed class State(open val message: CharSequence? = null)

class Loading(override val message: CharSequence? = null) : State()

class Error(override val message: CharSequence? = null, val code: Int, val extra: Any? = null) : State()

class Completed(override val message: CharSequence? = null, val extra: Any? = null) : State()

var View.state: State
    get() {
        val tag = this.getTag(R.id.state_tag)
        if (tag == null) {
            this.setTag(R.id.state_tag, Completed())
        }
        return this.getTag(R.id.state_tag) as State
    }
    set(value) {
        when (value) {
            is Error -> {
                this.loadingHandler?.dismissLoading()
                this.errorHandler?.onError(this, value)
            }
            is Completed -> {
                this.loadingHandler?.dismissLoading()
                this.completedHandler?.onCompleted(this, value)
            }
            is Loading -> {
                when (this.state) {
                    is Error -> {
                        this.errorHandler?.dismissError()
                    }
                    is Completed -> {
                        this.completedHandler?.dismissCompleted()
                    }
                }
                this.loadingHandler?.onLoading(this, value)
            }
        }
        this.setTag(R.id.state_tag, value)

    }


fun View.postState(state: State) {
    post {
        this.state = state
    }
}

var View.errorHandler: ErrorHandler?
    get() {
        return this.getTag(R.id.state_error_tag) as ErrorHandler?
    }
    set(value) {
        this.setTag(R.id.state_error_tag, value)
    }

var View.loadingHandler: LoadingHandler?
    get() {
        return this.getTag(R.id.state_loading_tag) as LoadingHandler?
    }
    set(value) {
        this.setTag(R.id.state_loading_tag, value)
    }

var View.completedHandler: CompletedHandler?
    get() {
        return this.getTag(R.id.state_completed_tag) as CompletedHandler?
    }
    set(value) {
        this.setTag(R.id.state_completed_tag, value)
    }




interface LoadingHandler {

    fun onLoading(view: View, loading: Loading): Boolean

    fun dismissLoading()
}

interface ErrorHandler {

    fun onError(view: View, error: Error): Boolean

    fun dismissError()
}


interface CompletedHandler {

    fun onCompleted(view: View, completed: Completed): Boolean

    fun dismissCompleted()
}