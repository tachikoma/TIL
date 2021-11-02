package com.example.composetutorial.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import android.view.MotionEvent


class InterceptTouchMapView : MapView {
    @kotlin.jvm.JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyle: Int = 0
    ) : super(context, attrs, defStyle)

    constructor(
        context: Context,
        options: GoogleMapOptions? = null
    ) : super(context, options)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN ->
                // Disallow parent (ScrollView, RecyclerView) to intercept touch events.
                this.parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP ->
                // Allow parent (ScrollView, RecyclerView) to intercept touch events.
                this.parent.requestDisallowInterceptTouchEvent(false)
        }

        // Handle MapView's touch events.
        return super.dispatchTouchEvent(ev)
    }
}