object Cube {

    val cubeVertexSize = 4L * 10L // Byte size of one cube vertex.
    val cubePositionOffset = 0L
    val cubeUVOffset = 4L * 8L
    val cubeVertexCount = 36

    val cubeVertexArray = arrayOf(
        // float4 position, float4 color, float2 uv,
        1, -1, 1, 1, 1, 0, 1, 1, 0, 1,
        -1, -1, 1, 1, 0, 0, 1, 1, 1, 1,
        -1, -1, -1, 1, 0, 0, 0, 1, 1, 0,
        1, -1, -1, 1, 1, 0, 0, 1, 0, 0,
        1, -1, 1, 1, 1, 0, 1, 1, 0, 1,
        -1, -1, -1, 1, 0, 0, 0, 1, 1, 0,

        1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
        1, -1, 1, 1, 1, 0, 1, 1, 1, 1,
        1, -1, -1, 1, 1, 0, 0, 1, 1, 0,
        1, 1, -1, 1, 1, 1, 0, 1, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
        1, -1, -1, 1, 1, 0, 0, 1, 1, 0,

        -1, 1, 1, 1, 0, 1, 1, 1, 0, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, -1, 1, 1, 1, 0, 1, 1, 0,
        -1, 1, -1, 1, 0, 1, 0, 1, 0, 0,
        -1, 1, 1, 1, 0, 1, 1, 1, 0, 1,
        1, 1, -1, 1, 1, 1, 0, 1, 1, 0,

        -1, -1, 1, 1, 0, 0, 1, 1, 0, 1,
        -1, 1, 1, 1, 0, 1, 1, 1, 1, 1,
        -1, 1, -1, 1, 0, 1, 0, 1, 1, 0,
        -1, -1, -1, 1, 0, 0, 0, 1, 0, 0,
        -1, -1, 1, 1, 0, 0, 1, 1, 0, 1,
        -1, 1, -1, 1, 0, 1, 0, 1, 1, 0,

        1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
        -1, 1, 1, 1, 0, 1, 1, 1, 1, 1,
        -1, -1, 1, 1, 0, 0, 1, 1, 1, 0,
        -1, -1, 1, 1, 0, 0, 1, 1, 1, 0,
        1, -1, 1, 1, 1, 0, 1, 1, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 0, 1,

        1, -1, -1, 1, 1, 0, 0, 1, 0, 1,
        -1, -1, -1, 1, 0, 0, 0, 1, 1, 1,
        -1, 1, -1, 1, 0, 1, 0, 1, 1, 0,
        1, 1, -1, 1, 1, 1, 0, 1, 0, 0,
        1, -1, -1, 1, 1, 0, 0, 1, 0, 1,
        -1, 1, -1, 1, 0, 1, 0, 1, 1, 0,
    ).let { FloatArray(it.size) { index -> it[index].toFloat() } }

}