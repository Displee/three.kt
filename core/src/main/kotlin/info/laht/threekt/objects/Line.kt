package info.laht.threekt.objects

import info.laht.threekt.core.*
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector3
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class Line @JvmOverloads constructor(

    geometry: BufferGeometry? = null,
    material: LineBasicMaterial? = null

) : Object3DImpl(), GeometryObject, MaterialObject {

    override var geometry = geometry ?: BufferGeometry()
    override var material = material ?: LineBasicMaterial()

    open fun computeLineDistances(): Line {

        val start = Vector3()
        val end = Vector3()

        // we assume non-indexed geometry
        if (geometry.index == null) {

            val positionAttribute = geometry.attributes.position!!
            val lineDistances = mutableListOf(0f)

            for (i in 1 until positionAttribute.count) {
                positionAttribute.toVector3(i - 1, start)
                positionAttribute.toVector3(i, end)

                lineDistances[i] = lineDistances[i - 1]
                lineDistances[i] += start.distanceTo(end)
            }

            geometry.addAttribute("lineDistance", FloatBufferAttribute(lineDistances.toFloatArray(), 1))

        } else {

            LOG.warn("computeLineDistances(): Computation only possible with non-indexed BufferGeometry.")

        }

        return this
    }

    override fun raycast(raycaster: Raycaster, intersects: MutableList<Intersection>) {
        val matrixWorld = this.matrixWorld
        val threshold = raycaster.params.line.threshold

        // early out if no position or empty attribute
        val position = geometry.attributes.position ?: return
        val localRay = raycaster.ray.clone().apply {
            // convert ray to local space
            val inverseMatrix = Matrix4().copy(matrixWorld).invert()
            applyMatrix4(inverseMatrix)
        }

        val start = Vector3()
        val end = Vector3()
        val interSegment = Vector3()
        val interRay = Vector3()

        val step = if (this is LineSegments) 2 else 1
        val endCount = position.count - 1

        var i = 0
        while (i < endCount) {

            start.fromArray(position.buffer, i * position.itemSize)
            end.fromArray(position.buffer, (i + 1) * position.itemSize)

            val distSq = localRay.distanceSqToSegment(
                start,
                end,
                interRay,
                interSegment
            )

            if (distSq <= threshold * threshold) {

                // convert intersection point back to world space
                interSegment.applyMatrix4(matrixWorld)

                val distance = raycaster.ray.origin.distanceTo(interSegment)
                if (distance < raycaster.near || distance > raycaster.far) {
                    i += step
                    continue
                }

                intersects.add(
                    Intersection(
                        distance = distance,
                        point = interSegment.clone(),
                        index = i,
                        face = null,
                        `object` = this
                    )
                )
            }
            i += step
        }
    }


    fun copy(source: Line): Line {
        super<Object3DImpl>.copy(source, false)

        this.geometry.copy(source.geometry)
        this.material.copy(source.material)

        return this
    }

    override fun clone(): Line {
        return Line(this.geometry, this.material).copy(this)
    }

    private companion object {

        val LOG: Logger = LoggerFactory.getLogger(Line::class.java)

    }

}
