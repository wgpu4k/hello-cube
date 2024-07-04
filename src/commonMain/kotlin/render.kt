import io.ygdrasil.wgpu.AutoClosableContext
import io.ygdrasil.wgpu.CanvasConfiguration
import io.ygdrasil.wgpu.WGPUContext


suspend fun AutoClosableContext.createScene(context: WGPUContext): RotatingCubeScene {
    context.surface.configure(
        CanvasConfiguration(
            context.device
        )
    )

    return RotatingCubeScene(context).also { scene ->
        context.bind()
        scene.bind()
        scene.initialize()
    }

}

