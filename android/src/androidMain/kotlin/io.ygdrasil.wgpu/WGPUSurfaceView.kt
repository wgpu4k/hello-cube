package io.ygdrasil.wgpu

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import io.ygdrasil.wgpu.androidContextRenderer

class WGPUSurfaceView : SurfaceView, SurfaceHolder.Callback2 {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        holder.addCallback(this)
        // The only way to set SurfaceView background color to transparent:
        // https://groups.google.com/g/android-developers/c/jYjvm7ItpXQ?pli=1
        //this.setZOrderOnTop(true)
        //holder.setFormat(PixelFormat.TRANSPARENT)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        MainScope().launch {
            try {
                with(context!!) {
                    val androidContext = androidContextRenderer(surfaceHolder, width, height)
                    setWillNotDraw(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        println("surfaceDestroyed")

    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        MainScope().launch {
            try {
                invalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun changeExample(index: Int) {
        println("changeExample with index $index")
        // TODO
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) { }

}