package com.iti.fineweather.features.alerts.helpers;

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayWindow @Inject constructor(@ApplicationContext private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val windowParams = WindowManager.LayoutParams(
        0,
        0,
        0,
        0,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.TRANSLUCENT
    )

    private var rootView: View? = null

    init {
        initWindowParams()
    }

    private fun initWindowParams() {
        calculateSizeAndPosition(windowParams)
    }

    private fun calculateSizeAndPosition(params: WindowManager.LayoutParams) {
        params.gravity = Gravity.TOP or Gravity.CENTER
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.x = 0
        params.y = 0
    }

    fun open(content: @Composable () -> Unit) {
        try {
            if (rootView != null) close()
            initView(content)
            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            Timber.e(e)
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView(content: @Composable () -> Unit) {
        val lifecycleOwner = WindowLifecycleOwner()
        val viewModelStore = ViewModelStore()
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        rootView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(viewModelStoreOwner)

            setContent {
                content()
            }
        }
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun close() {
        try {
            windowManager.removeView(rootView)
            rootView = null
        } catch (e: Exception) {
            Timber.e(e)
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }


    internal class WindowLifecycleOwner : SavedStateRegistryOwner {
        private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
        private var mSavedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)

        override val lifecycle: Lifecycle
            get() = mLifecycleRegistry
        override val savedStateRegistry: SavedStateRegistry
            get() = mSavedStateRegistryController.savedStateRegistry

        fun setCurrentState(state: Lifecycle.State) {
            mLifecycleRegistry.currentState = state
        }

        fun handleLifecycleEvent(event: Lifecycle.Event) {
            mLifecycleRegistry.handleLifecycleEvent(event)
        }

        fun performRestore(savedState: Bundle?) {
            mSavedStateRegistryController.performRestore(savedState)
        }

        fun performSave(outBundle: Bundle) {
            mSavedStateRegistryController.performSave(outBundle)
        }
    }
}

