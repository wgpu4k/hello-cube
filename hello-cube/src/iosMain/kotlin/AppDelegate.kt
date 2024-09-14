@file:OptIn(ExperimentalForeignApi::class)

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow

class AppDelegate {

    private var window: UIWindow? = null

    fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?
    ): Boolean {
        println("Hello cube")
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        return true
    }


}
