package kr.ds.helper.util

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class BottomSheetHelper(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) {

    private var bottomSheetDialogFragment: BottomSheetDialogFragment? = null

    private val lifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResumed() {
            Timber.d("onResumed")
            bottomSheetDialogFragment?.let {
                it.show(fragmentManager, it.tag)
                bottomSheetDialogFragment = null
            }
            lifecycle.removeObserver(this)
        }
    }

    fun show(bottomSheetDialogFragment: BottomSheetDialogFragment) {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            this.bottomSheetDialogFragment = bottomSheetDialogFragment
            lifecycle.addObserver(lifecycleObserver)
            return
        }

        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.tag)
    }
}