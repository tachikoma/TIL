package kr.ds.helper.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.ds.helper.R
import kr.ds.helper.util.getScreenHeightToPx
import kr.ds.helper.util.toPx
import timber.log.Timber

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @SuppressLint("RestrictedApi")
    final override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { setupBottomSheet(it) }
    }

    private val headerHeight = resources.getDimension(R.dimen.bottom_sheet_header_height).toPx

    /**
     * 디바이스 높이값을 가져와서 헤더 높이 만큼 뺀 높이가 BottomSheet 의 높이가 된다.
     */
    private fun bottomSheetMaxHeight() =
        (getScreenHeightToPx() * maxRatio/**/ - headerHeight/**/).toInt()

    /**
     * 최대 높이 고정 여부, 기본값 : 크기가 늘어날 경우 화면 최대로(false)
     *
     * TODO true 로 할 경우 headerHeight 반영하여 딜레이 되면서 리사이즈 됨, 떠 있는 중에 크기가 바뀌면 반영이 안됨
     */
    open val shouldFixedMaxHeight: Boolean = false

    /**
     * 드래그 여부, 기본값 : 드래그 불가능(false)
     */
    open val isDraggable: Boolean = false

    /**
     * 외부 클릭시 닫기 여부 설정, 기본값 : 닫지 않음(false)
     */
    open val shouldCancellableOnTouchOutside = false

    /**
     *
     */
    abstract val dialogResourceId: Int

    /**
     * 공통 BottomSheet Theme 적용
     */
    override fun getTheme(): Int {
        return R.style.CustomThemeBottomSheet
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )
        bottomSheetDialog.setCanceledOnTouchOutside(shouldCancellableOnTouchOutside)
        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)

            val behavior = BottomSheetBehavior.from(it)
            behavior.isDraggable = isDraggable
            if (shouldFixedMaxHeight) {
                val maxHeight = bottomSheetMaxHeight()

                Timber.tag("Height").d("${it.measuredHeight}")
                if (it.measuredHeight > 0 && it.measuredHeight > maxHeight) {
                    it.layoutParams.height = maxHeight
                } else {
                    it.layoutParams.height
                }
            }
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
            val listener = object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    Timber.tag("Height").d("bottomSheet onGlobalLayout ${it.measuredHeight}")
                    it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(250)
                        it.requestLayout()
                    }
                }
            }
            it.viewTreeObserver.addOnGlobalLayoutListener(listener)

            val layoutListener = object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    Timber.tag("Height").d("bottomSheet onLayoutChange ${it.measuredHeight}")
                    it.removeOnLayoutChangeListener(this)
                }

            }
            it.addOnLayoutChangeListener(layoutListener)
        } ?: run {
            dismiss()
        }
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(dialogResourceId, container, false)

    companion object {

        private const val maxRatio = 1.0f
    }
}