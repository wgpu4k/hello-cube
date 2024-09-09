import io.ygdrasil.wgpu.WGPU.Companion.loadLibrary
import io.ygdrasil.wgpu.autoClosableContext
import io.ygdrasil.wgpu.glfwContextRenderer
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.GLFW.*

// ~60 Frame per second
val UPDATE_INTERVAL = (1000.0 / 60.0).toInt()

fun main() {
    runBlocking {
        loadLibrary()
        initLog()
        val glfwContext = glfwContextRenderer(
            width = 800,
            height = 600,
            title = "Hello cube"
        )

        autoClosableContext {
            val scene = createScene(glfwContext.wgpuContext)
            glfwShowWindow(glfwContext.windowHandler)
            var lastGameTick = System.currentTimeMillis()

            while (!glfwWindowShouldClose(glfwContext.windowHandler)) {
                glfwPollEvents()
                val diff = System.currentTimeMillis() - lastGameTick
                if (diff > UPDATE_INTERVAL) {
                    lastGameTick = System.currentTimeMillis()
                    autoClosableContext {
                        with(scene) { render() }
                        glfwContext.wgpuContext.surface.present()
                        scene.frame += 1
                    }
                }
            }
        }
        glfwContext.close()
    }
}