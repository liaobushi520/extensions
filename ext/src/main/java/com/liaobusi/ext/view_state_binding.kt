package com.liaobusi.ext

import android.view.View
import androidx.databinding.BindingAdapter
import com.liaobusi.ktx.*


@BindingAdapter("errorHandler")
fun errorHandler(view: View, errorHandler: ErrorHandler) {
    view.errorHandler = errorHandler
}

@BindingAdapter("loadingHandler")
fun loadingHandler(view: View, loadingHandler: LoadingHandler) {
    view.loadingHandler = loadingHandler
}

@BindingAdapter("completedHandler")
fun completedHandler(view: View, completedHandler: CompletedHandler) {
    view.completedHandler = completedHandler
}




