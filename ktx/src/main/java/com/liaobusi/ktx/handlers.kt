package com.liaobusi.ktx

import android.app.ProgressDialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

class ToastHandler : CompletedHandler, ErrorHandler, LoadingHandler {
    override fun showLoading(view: View, loading: Loading) {
        Toast.makeText(view.context, loading.message, Toast.LENGTH_SHORT).show()
    }

    override fun dismissLoading() {
    }

    override fun showError(view: View, error: Error) {
        Toast.makeText(view.context, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun dismissError() {
    }

    override fun showCompleted(view: View, completed: Completed) {
        Toast.makeText(view.context, completed.message, Toast.LENGTH_SHORT).show()
    }

    override fun dismissCompleted() {
    }

}

class ViewEnableHandler : CompletedHandler, ErrorHandler, LoadingHandler {
    override fun showLoading(view: View, loading: Loading) {
        view.isEnabled = false
    }

    override fun dismissLoading() {
    }

    override fun showError(view: View, error: Error) {
        view.isEnabled = true
    }

    override fun dismissError() {

    }

    override fun showCompleted(view: View, completed: Completed) {
        view.isEnabled = true
    }

    override fun dismissCompleted() {

    }
}


class ErrorViewHandler(
    private val errorView: View,
    private val attachToRootView: Boolean = true,
    private val setupError: View.(error: Error) -> Unit
) : ErrorHandler {
    override fun showError(view: View, error: Error) {
        val root: ViewGroup
        if (attachToRootView) {
            root = findRootFrameLayout(view) as FrameLayout
        } else {
            if (view !is ViewGroup) {
                throw IllegalStateException("view must be FrameLayout or ConstraintLayout")
            }
            root = view
        }
        errorView.setupError(error)
        errorView.detachFromParent()
        when (root) {
            is FrameLayout -> {
                val lp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                lp.gravity = Gravity.CENTER
                root.addView(errorView, lp)
            }
            is ConstraintLayout -> {
                val lp = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                root.addView(errorView, lp)
            }

        }
    }

    override fun dismissError() {

    }


}


class LoadingDialogHandler() : LoadingHandler {
    private var dialog: ProgressDialog? = null
    override fun showLoading(view: View, loading: Loading) {
        dialog = ProgressDialog(view.context)
        dialog?.isIndeterminate = true
        dialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog?.setCancelable(false)
        dialog?.show()
    }

    override fun dismissLoading() {
        dialog?.dismiss()
    }

}
