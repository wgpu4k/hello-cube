package io.ygdrasil.wgpu

import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surfaceView = WGPUSurfaceView(context = this)
        addContentView(surfaceView, surfaceView.layoutParams)

    }
}
