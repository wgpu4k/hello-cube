import Cube.cubePositionOffset
import Cube.cubeUVOffset
import Cube.cubeVertexArray
import Cube.cubeVertexCount
import Cube.cubeVertexSize
import io.ygdrasil.wgpu.AutoClosableContext
import io.ygdrasil.wgpu.BindGroup
import io.ygdrasil.wgpu.BindGroupDescriptor
import io.ygdrasil.wgpu.Buffer
import io.ygdrasil.wgpu.BufferDescriptor
import io.ygdrasil.wgpu.BufferUsage
import io.ygdrasil.wgpu.Color
import io.ygdrasil.wgpu.CompareFunction
import io.ygdrasil.wgpu.CullMode
import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.LoadOp
import io.ygdrasil.wgpu.PrimitiveTopology
import io.ygdrasil.wgpu.RenderPassDescriptor
import io.ygdrasil.wgpu.RenderPipeline
import io.ygdrasil.wgpu.RenderPipelineDescriptor
import io.ygdrasil.wgpu.RenderingContext
import io.ygdrasil.wgpu.ShaderModuleDescriptor
import io.ygdrasil.wgpu.Size3D
import io.ygdrasil.wgpu.StoreOp
import io.ygdrasil.wgpu.TextureDescriptor
import io.ygdrasil.wgpu.TextureFormat
import io.ygdrasil.wgpu.TextureUsage
import io.ygdrasil.wgpu.VertexFormat
import io.ygdrasil.wgpu.WGPUContext
import io.ygdrasil.wgpu.beginRenderPass
import korlibs.math.geom.Angle
import korlibs.math.geom.Matrix4
import kotlin.math.PI

class RotatingCubeScene(val context: WGPUContext) : AutoCloseable {

    var frame = 0
    protected val autoClosableContext = AutoClosableContext()

    internal val device: Device
        get() = context.device

    internal val renderingContext: RenderingContext
        get() = context.renderingContext

    lateinit var renderPipeline: RenderPipeline
    lateinit var projectionMatrix: Matrix4
    lateinit var renderPassDescriptor: RenderPassDescriptor
    lateinit var uniformBuffer: Buffer
    lateinit var uniformBindGroup: BindGroup
    lateinit var verticesBuffer: Buffer

    fun initialize() = with(autoClosableContext) {

        // Create dummy texture, as we manipulate immutable data and we need to assign a texture early
        val dummyTexture by lazy {
            device.createTexture(
                TextureDescriptor(
                    size = Size3D(1, 1),
                    format = TextureFormat.depth24plus,
                    usage = setOf(TextureUsage.renderattachment),
                )
            ).also { with(autoClosableContext) { it.bind() } }
        }

        // Create a vertex buffer from the cube data.
        verticesBuffer = device.createBuffer(
            BufferDescriptor(
                size = (cubeVertexArray.size * Float.SIZE_BYTES).toLong(),
                usage = setOf(BufferUsage.vertex),
                mappedAtCreation = true
            )
        ).bind()

        // Util method to use getMappedRange
        verticesBuffer.mapFrom(cubeVertexArray)
        verticesBuffer.unmap()

        renderPipeline = device.createRenderPipeline(
            RenderPipelineDescriptor(
                vertex = RenderPipelineDescriptor.VertexState(
                    module = device.createShaderModule(
                        ShaderModuleDescriptor(
                            code = basicVertexShader
                        )
                    ).bind(), // bind to autoClosableContext to release it later
                    buffers = listOf(
                        RenderPipelineDescriptor.VertexState.VertexBufferLayout(
                            arrayStride = cubeVertexSize,
                            attributes = listOf(
                                RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute(
                                    shaderLocation = 0,
                                    offset = cubePositionOffset,
                                    format = VertexFormat.float32x4
                                ),
                                RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute(
                                    shaderLocation = 1,
                                    offset = cubeUVOffset,
                                    format = VertexFormat.float32x2
                                )
                            )
                        )
                    )
                ),
                fragment = RenderPipelineDescriptor.FragmentState(
                    module = device.createShaderModule(
                        ShaderModuleDescriptor(
                            code = vertexPositionColorShader
                        )
                    ).bind(), // bind to autoClosableContext to release it later
                    targets = listOf(
                        RenderPipelineDescriptor.FragmentState.ColorTargetState(
                            format = renderingContext.textureFormat
                        )
                    )
                ),
                primitive = RenderPipelineDescriptor.PrimitiveState(
                    topology = PrimitiveTopology.trianglelist,
                    cullMode = CullMode.back
                ),
                depthStencil = RenderPipelineDescriptor.DepthStencilState(
                    depthWriteEnabled = true,
                    depthCompare = CompareFunction.less,
                    format = TextureFormat.depth24plus
                ),
                multisample = RenderPipelineDescriptor.MultisampleState(
                    count = 1,
                    mask = 0xFFFFFFFu
                )
            )
        ).bind()

        val depthTexture = device.createTexture(
            TextureDescriptor(
                size = Size3D(renderingContext.width, renderingContext.height),
                format = TextureFormat.depth24plus,
                usage = setOf(TextureUsage.renderattachment),
            )
        ).bind()

        val uniformBufferSize = 4L * 16L; // 4x4 matrix
        uniformBuffer = device.createBuffer(
            BufferDescriptor(
                size = uniformBufferSize,
                usage = setOf(BufferUsage.uniform, BufferUsage.copydst)
            )
        ).bind()

        uniformBindGroup = device.createBindGroup(
            BindGroupDescriptor(
                layout = renderPipeline.getBindGroupLayout(0),
                entries = listOf(
                    BindGroupDescriptor.BindGroupEntry(
                        binding = 0,
                        resource = BindGroupDescriptor.BufferBinding(
                            buffer = uniformBuffer
                        )
                    )
                )
            )
        ).bind()

        renderPassDescriptor = RenderPassDescriptor(
            colorAttachments = listOf(
                RenderPassDescriptor.ColorAttachment(
                    view = dummyTexture.createView().bind(), // Assigned later
                    loadOp = LoadOp.clear,
                    clearValue = Color(0.5, 0.5, 0.5, 1.0),
                    storeOp = StoreOp.store,
                )
            ),
            depthStencilAttachment = RenderPassDescriptor.DepthStencilAttachment(
                view = depthTexture.createView(),
                depthClearValue = 1.0f,
                depthLoadOp = LoadOp.clear,
                depthStoreOp = StoreOp.store
            )
        )


        val aspect = renderingContext.width / renderingContext.height.toDouble()
        val fox = Angle.fromRadians((2 * PI) / 5)
        projectionMatrix = Matrix4.perspective(fox, aspect, 1.0, 100.0)
    }

    fun AutoClosableContext.render() {

        val transformationMatrix = getTransformationMatrix(
            frame / 100.0,
            projectionMatrix
        )
        device.queue.writeBuffer(
            uniformBuffer,
            0,
            transformationMatrix,
            0,
            transformationMatrix.size.toLong()
        )

        renderPassDescriptor = renderPassDescriptor.copy(
            colorAttachments = listOf(
                renderPassDescriptor.colorAttachments[0].copy(
                    view = renderingContext.getCurrentTexture()
                        .bind()
                        .createView()
                )
            )
        )

        val encoder = device.createCommandEncoder()
            .bind()

        encoder.beginRenderPass(renderPassDescriptor) {
            setPipeline(renderPipeline)
            setBindGroup(0, uniformBindGroup)
            setVertexBuffer(0, verticesBuffer)
            draw(cubeVertexCount)
            end()
        }

        val commandBuffer = encoder.finish()
            .bind()

        device.queue.submit(listOf(commandBuffer))

    }


    override fun close() {
        autoClosableContext.close()
    }
}


private fun getTransformationMatrix(angle: Double, projectionMatrix: Matrix4): FloatArray {
    var viewMatrix = Matrix4.IDENTITY
    viewMatrix = viewMatrix.translated(0, 0, -5)

    viewMatrix = viewMatrix.rotated(
        Angle.fromRadians(Angle.fromRadians(angle).sine),
        Angle.fromRadians(Angle.fromRadians(angle).cosine),
        Angle.fromRadians(0)
    )

    return (projectionMatrix * viewMatrix).copyToColumns()
}