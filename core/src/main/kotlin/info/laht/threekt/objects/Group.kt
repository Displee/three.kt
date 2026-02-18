package info.laht.threekt.objects

import info.laht.threekt.core.Intersection
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.core.Raycaster

class Group : Object3DImpl() {
    override fun raycast(raycaster: Raycaster, intersects: MutableList<Intersection>) {
        // empty
    }
}
