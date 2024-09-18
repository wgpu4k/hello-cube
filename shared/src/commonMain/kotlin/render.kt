import io.ygdrasil.wgpu.AutoClosableContext
import io.ygdrasil.wgpu.CanvasConfiguration
import io.ygdrasil.wgpu.CompositeAlphaMode
import io.ygdrasil.wgpu.TextureUsage
import io.ygdrasil.wgpu.WGPUContext


fun AutoClosableContext.createScene(context: WGPUContext): RotatingCubeScene {

    val alphaMode = CompositeAlphaMode.inherit?.takeIf { context.surface.supportedAlphaMode.contains(it) }
        ?: CompositeAlphaMode.opaque

    context.surface
        .configure(
            CanvasConfiguration(
                device = context.device,
                format = context.renderingContext.textureFormat,
                usage = setOf(TextureUsage.renderattachment, TextureUsage.copysrc),
                alphaMode = alphaMode
            )
        )

    return RotatingCubeScene(context).also { scene ->
        context.bind()
        scene.bind()
        scene.initialize()
    }

}

