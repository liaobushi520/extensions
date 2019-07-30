package com.liaobusi.extensions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.liaobusi.ktx.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {

            it.loadingHandler = LoadingDialogHandler()//LoadingViewHandler(this, attachToRootView = true)

            it.completedHandler = ToastHandler()

            Thread {
                it.postState(Loading("加载中..."))
                Thread.sleep(10000)
                it.postState(fetch())
            }.start()
            fetch()


        }
    }


    fun fetch(): State {
        return Error("sss", 1111, "")
    }
}
