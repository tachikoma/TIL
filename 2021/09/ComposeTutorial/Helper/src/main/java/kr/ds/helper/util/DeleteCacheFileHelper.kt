package kr.ds.helper.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber
import java.io.File

class DeleteCacheFileHelper(private val lifecycle: Lifecycle) {

    private val cacheFileList: MutableList<File> = mutableListOf()

    private val lifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroyed() {
            Timber.d("onDestroyed")
            cacheFileList.forEach {
                if (!it.delete()) {
                    Timber.w("delete fail ${it.absolutePath}")
                } else {
                    Timber.w("deleted ${it.absolutePath}")
                }
            }
            lifecycle.removeObserver(this)
        }
    }

    init {
        lifecycle.addObserver(lifecycleObserver)
    }

    fun add(file: File) {
        cacheFileList.add(file)
    }
}