package com.liaobusi.ext

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.liaobusi.ktx.*


class ToastHandler : CompletedHandler, ErrorHandler, LoadingHandler {


    override fun onLoading(view: View, loading: Loading): Boolean {
        Toast.makeText(view.context, loading.message, Toast.LENGTH_SHORT).show()
        return false
    }

    override fun dismissLoading() {
    }

    override fun onError(view: View, error: Error): Boolean {
        Toast.makeText(view.context, error.message, Toast.LENGTH_SHORT).show()
        return false
    }


    override fun dismissError() {
    }

    override fun onCompleted(view: View, completed: Completed): Boolean {
        Toast.makeText(view.context, completed.message, Toast.LENGTH_SHORT).show()
        return false
    }

    override fun dismissCompleted() {
    }

}

class ViewEnableHandler : CompletedHandler, ErrorHandler, LoadingHandler {


    override fun onError(view: View, error: Error): Boolean {
        view.isEnabled = true
        return false
    }

    override fun onLoading(view: View, loading: Loading): Boolean {
        view.isEnabled = false
        return false
    }

    override fun dismissLoading() {
    }


    override fun dismissError() {

    }

    override fun onCompleted(view: View, completed: Completed): Boolean {
        view.isEnabled = true
        return false
    }

    override fun dismissCompleted() {

    }
}

private class ReplaceFragment(
    private val layoutId: Int = R.layout.fragment_replace,
    private val retry: (() -> Unit)? = null
) : Fragment() {

    var progressView: View? = null

    var errorView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(layoutId, container, false)
        progressView = root.findViewById(R.id.progressView)
        errorView = root.findViewById(R.id.errorView)
        val retryBtn = root.findViewById<View>(R.id.retryBtn)
        retryBtn.setOnClickListener {
            retry?.invoke()
        }
        return root
    }


    internal fun adjustViews(state: State) {
        when (state) {
            is Loading -> {
                progressView?.visibility = View.VISIBLE
                errorView?.visibility = View.GONE
            }
            is Error -> {
                progressView?.visibility = View.GONE
                errorView?.visibility = View.VISIBLE
            }
        }
    }
}


class FragmentReplaceHandler(
    private val parentFragmentManager: FragmentManager,
    private val containerId: Int? = null,
    private val layoutId: Int = R.layout.fragment_replace,
    private val retry: (() -> Unit)? = null
) : CompletedHandler, ErrorHandler, LoadingHandler {

    private lateinit var replaceFragment: ReplaceFragment

    private fun ensureAttachToParent(view: View) {
        if (!this::replaceFragment.isInitialized) {
            replaceFragment = ReplaceFragment(layoutId, retry = retry)
        }
        if (!replaceFragment.isAdded) {
            val containerId = if (containerId == null) {
                containerId
            } else {
                findRootFrameLayout(view)?.id
            }
            if (containerId == null) {
                throw IllegalStateException("containerId is null ,so we find root view.but not found")
            } else {
                parentFragmentManager.beginTransaction().add(containerId, replaceFragment).hide(replaceFragment)
                    .commitNow()
            }
        }
    }


    override fun onLoading(view: View, loading: Loading): Boolean {
        ensureAttachToParent(view)
        if (replaceFragment.isHidden) {
            parentFragmentManager.beginTransaction().show(replaceFragment).commitNow()
        }
        replaceFragment.adjustViews(loading)
        return false
    }

    override fun dismissLoading() {}

    override fun onError(view: View, error: Error): Boolean {
        ensureAttachToParent(view)
        if (replaceFragment.isHidden) {
            parentFragmentManager.beginTransaction().show(replaceFragment).commitNow()
        }
        replaceFragment.adjustViews(error)
        return false
    }


    override fun dismissError() {

    }

    override fun onCompleted(view: View, completed: Completed): Boolean {
        ensureAttachToParent(view)
        parentFragmentManager.beginTransaction().hide(replaceFragment).commitNow()
        return false
    }

    override fun dismissCompleted() {}

}


class ErrorViewHandler(
    private val errorView: View,
    private val attachToRootView: Boolean = true,
    private val setupError: View.(error: Error) -> Unit
) : ErrorHandler {
    override fun onError(view: View, error: Error): Boolean {
        val root: ViewGroup
        if (attachToRootView) {
            root = findRootFrameLayout(view) as FrameLayout
        } else {
            if (view !is ViewGroup) {
                throw IllegalStateException("view must be ViewGroup")
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
            else -> {
                throw IllegalStateException("view must be FrameLayout or ConstraintLayout")
            }

        }
        return false
    }

    override fun dismissError() {
        errorView.detachFromParent()
    }


}


class LoadingDialogHandler() : LoadingHandler {
    private var dialog: ProgressDialog? = null
    override fun onLoading(view: View, loading: Loading): Boolean {
        dialog = ProgressDialog(view.context)
        dialog?.isIndeterminate = true
        dialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog?.setCancelable(false)
        dialog?.show()
        return false
    }

    override fun dismissLoading() {
        dialog?.dismiss()
    }

}


class LoadingViewHandler(context: Context, private val attachToRootView: Boolean = true) : LoadingHandler {

    private val progressView = ProgressBar(context).apply {
        isIndeterminate = true
    }

    override fun onLoading(view: View, loading: Loading): Boolean {

        val root: ViewGroup?
        if (attachToRootView) {
            root = findRootFrameLayout(view) as FrameLayout
        } else {
            if (view !is ViewGroup) {
                throw IllegalStateException("view must be FrameLayout or ConstraintLayout")
            }
            root = view
        }
        progressView.detachFromParent()
        when (root) {
            is FrameLayout -> {
                val lp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                lp.gravity = Gravity.CENTER
                root.addView(progressView, lp)
            }
            is ConstraintLayout -> {
                val lp = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                lp.leftToLeft = 0
                lp.rightToRight = 0
                lp.topToTop = 0
                lp.bottomToBottom = 0
                root.addView(progressView, lp)
            }
        }
        return false
    }

    override fun dismissLoading() {
        progressView.detachFromParent()
    }

}


class ComposeHandler(
    private val loadingHandlers: List<LoadingHandler>? = null,
    private val completedHandlers: List<CompletedHandler>? = null,
    private val errorHandlers: List<ErrorHandler>? = null
) : LoadingHandler, CompletedHandler,
    ErrorHandler {
    override fun onLoading(view: View, loading: Loading): Boolean {
        if (!loadingHandlers.isNullOrEmpty()) {
            for (loadingHandler in loadingHandlers) {
                val consumed = loadingHandler.onLoading(view, loading)
                if (consumed) {
                    return true
                }
            }
        }
        return false
    }

    override fun dismissLoading() {
        loadingHandlers?.forEach { it.dismissLoading() }
    }

    override fun onError(view: View, error: Error): Boolean {
        if (!errorHandlers.isNullOrEmpty()) {
            for (errorHandler in errorHandlers) {
                val consumed = errorHandler.onError(view, error)
                if (consumed) {
                    return true
                }
            }
        }
        return false
    }

    override fun dismissError() {
        errorHandlers?.forEach { it.dismissError() }
    }

    override fun onCompleted(view: View, completed: Completed): Boolean {
        if (!completedHandlers.isNullOrEmpty()) {
            for (completedHandler in completedHandlers) {
                val consumed = completedHandler.onCompleted(view, completed)
                if (consumed) {
                    return true
                }
            }
        }
        return false
    }

    override fun dismissCompleted() {
        completedHandlers?.forEach { it.dismissCompleted() }
    }

}

internal fun findRootFrameLayout(view: View): FrameLayout? {
    var parent = view.parent
    var frameLayout: FrameLayout? = null
    while (parent != null) {
        if (parent is FrameLayout) {
            frameLayout = parent
        }
        parent = parent.parent
    }
    return frameLayout
}


