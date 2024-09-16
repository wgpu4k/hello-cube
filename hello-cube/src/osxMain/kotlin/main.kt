@file:OptIn(ExperimentalForeignApi::class)

import io.ygdrasil.wgpu.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import glfw.*

fun main() {
    runBlocking {
        val glfwContext = glfwContextRenderer(
            width = 800,
            height = 600,
            title = "Hello cube"
        )

        autoClosableContext {
            val scene = createScene(glfwContext.wgpuContext)
            glfwShowWindow(glfwContext.windowHandler)

            while (glfwWindowShouldClose(glfwContext.windowHandler) != 1) {
                glfwPollEvents()
                autoClosableContext {
                    with(scene) { render() }
                    glfwContext.wgpuContext.surface.present()
                    scene.frame += 1
                }
            }
        }
        glfwContext.close()
    }
}