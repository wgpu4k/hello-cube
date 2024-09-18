@file:OptIn(ExperimentalForeignApi::class)

import io.ygdrasil.wgpu.AutoClosableContext
import io.ygdrasil.wgpu.autoClosableContext
import io.ygdrasil.wgpu.iosContextRenderer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.runBlocking
import platform.CoreGraphics.CGSize
import platform.MetalKit.MTKView
import platform.MetalKit.MTKViewDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIScreen
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject

class AppDelegate {

    private var window: UIWindow? = null
    private var viewDelegate: ViewDelegate? = null

    fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?
    ): Boolean {
        window = UIWindow(frame = UIScreen.mainScreen.bounds).also { window ->
            UIViewController().also { controller ->
                MTKView().also { view ->
                    UIScreen.mainScreen.nativeScale
                    controller.view = view
                    window.rootViewController = controller
                    runBlocking {
                        configureApplication(
                            view,
                            UIScreen.mainScreen.bounds.useContents {
                                (size.width * UIScreen.mainScreen.nativeScale).toInt() to (size.height * UIScreen.mainScreen.nativeScale).toInt()
                            }
                        )
                    }
                }
            }
        }
        window?.makeKeyAndVisible()
        return true
    }

    val globalAutoClosableContext = AutoClosableContext()

    suspend fun configureApplication(view: MTKView, size: Pair<Int, Int>): RotatingCubeScene {
        try {
            val (width, height) = size
            val context = iosContextRenderer(
                view,
                width, height
            )
            val scene = globalAutoClosableContext.createScene(context.wgpuContext)
            viewDelegate = ViewDelegate(scene)
            view.delegate = viewDelegate
            return scene
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }

    }

}

class ViewDelegate(
    val scene: RotatingCubeScene,
) : NSObject(), MTKViewDelegateProtocol {

    override fun mtkView(view: MTKView, drawableSizeWillChange: CValue<CGSize>) { }

    override fun drawInMTKView(view: MTKView) {
        runBlocking {
            autoClosableContext {
                with(scene) { render() }
                scene.context.surface.present()
                scene.frame += 1
            }
        }
    }

}