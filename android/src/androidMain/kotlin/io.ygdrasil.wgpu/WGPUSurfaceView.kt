package io.ygdrasil.wgpu

import RotatingCubeScene
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import createScene
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class WGPUSurfaceView : SurfaceView, SurfaceHolder.Callback2 {

    val autoClosableContext = AutoClosableContext()
    var scene: RotatingCubeScene? = null
    var androidContext: AndroidContext? = null

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
            if (scene != null) return@launch
            try {
                with(context!!) {
                    androidContext = androidContextRenderer(surfaceHolder, width, height)
                    scene = autoClosableContext.createScene(androidContext!!.wgpuContext)
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
                autoClosableContext {
                    scene?.let {scene ->
                        with(scene) { render() }
                        scene.frame += 1
                    }
                    androidContext?.wgpuContext?.surface
                        ?.present()

                }
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