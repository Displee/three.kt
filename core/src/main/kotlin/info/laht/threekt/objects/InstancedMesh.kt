package info.laht.threekt.objects

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.Intersection
import info.laht.threekt.core.Raycaster
import info.laht.threekt.materials.Material
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix4

class InstancedMesh(
    geometry: BufferGeometry? = null,
    materials: MutableList<Material>? = null,
    var count: Int,
    var useColorAlphas: Boolean = false
) : Mesh(geometry, materials) {

    val instanceMatrix: FloatBufferAttribute = FloatBufferAttribute(FloatArray(count * 16), 16)
    var instanceColor: FloatBufferAttribute? = null
    private var colorsItemSize = if (useColorAlphas) 4 else 3
    override var frustumCulled = false

    fun copy(source: InstancedMesh): InstancedMesh {
        super.copy(source)
        instanceMatrix.copy(source.instanceMatrix)
        instanceColor = source.instanceColor?.clone()
        count = source.count
        return this
    }

    fun getColorAt(index: Int, color: Color) {
        val instanceColor = this.instanceColor
        checkNotNull(instanceColor) { "Instance color is null." }
        color.fromArray(instanceColor.buffer, index * colorsItemSize)
    }

    fun getMatrixAt(index: Int, matrix: Matrix4) {
        matrix.fromArray(instanceMatrix.buffer, index * 16)
    }

    fun setColorAt(index: Int, color: Color, opacity: Float = 1F) {
        if (instanceColor === null) {
            instanceColor = FloatBufferAttribute(FloatArray(count * colorsItemSize), colorsItemSize)
        }
        color.toArray(instanceColor!!.buffer, index * colorsItemSize)
        if (useColorAlphas) {
            instanceColor?.set(index * colorsItemSize + 3, opacity)
        }
    }

    fun setMatrixAt(index: Int, matrix: Matrix4) {
        matrix.toArray(instanceMatrix.buffer, index * 16)
    }

    override fun raycast(raycaster: Raycaster, intersects: MutableList<Intersection>) {
        val matrixWorld = matrixWorld
        val raycastTimes = count

        _mesh.geometry = geometry
        _mesh.material = material

        if (_mesh.materials.isEmpty()) {
            return
        }

        for (instanceId in 0 until raycastTimes) {

            // calculate the world matrix for each instance

            getMatrixAt(instanceId, _instanceLocalMatrix)

            _instanceWorldMatrix.multiplyMatrices(matrixWorld, _instanceLocalMatrix)

            // the mesh represents this single instance

            _mesh.matrixWorld.copy(_instanceWorldMatrix)

            _mesh.raycast(raycaster, _instanceIntersects)

            // process the result of raycast

            for (intersect in _instanceIntersects) {
                intersect.instanceId = instanceId
                intersect.`object` = this
                intersects.add(intersect)
            }

            _instanceIntersects.clear()
        }
    }

    fun dispose() {
        dispatchEvent("dispose", this)
    }

    companion object {
        private val _instanceLocalMatrix = Matrix4()
        private val _instanceWorldMatrix = Matrix4()
        private val _instanceIntersects = mutableListOf<Intersection>()
        private val _mesh = Mesh()
    }

}
