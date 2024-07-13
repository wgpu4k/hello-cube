import io.ygdrasil.wgpu.AutoClosableContext
import io.ygdrasil.wgpu.autoClosableContext
import io.ygdrasil.wgpu.canvasContextRenderer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

external fun setInterval(render: () -> Unit, updateInterval: Int)

// ~60 Frame per second
val UPDATE_INTERVAL = (1000.0 / 60.0).toInt()

fun main() {
    MainScope().launch {
        val globalAutoClosableContext = AutoClosableContext()
        val canvas = canvasContextRenderer(width = 800, height = 600)

        val scene = globalAutoClosableContext.createScene(canvas.wgpuContext)

        // Schedule main loop to run repeatedly
        setInterval({
            MainScope().launch {
                autoClosableContext {
                    with(scene) { render() }
                    scene.frame += 1
                }
            }
        }, UPDATE_INTERVAL)

    }
}