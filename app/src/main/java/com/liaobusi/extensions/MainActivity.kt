package com.liaobusi.extensions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.liaobusi.ktx.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lp=(btn.layoutParams as ConstraintLayout.LayoutParams)

        Log.e("LP",lp.leftToLeft.toString()+"")
        btn.setOnClickListener {
            val handler = FragmentReplaceHandler(R.id.container, supportFragmentManager)

            it.loadingHandler = handler//LoadingDialogHandler()//LoadingViewHandler(this, attachToRootView = true)

            it.completedHandler = handler

            it.errorHandler = handler

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
