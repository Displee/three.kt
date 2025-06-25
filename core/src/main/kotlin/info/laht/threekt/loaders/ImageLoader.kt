package info.laht.threekt.loaders

import info.laht.threekt.textures.Image
import org.lwjgl.BufferUtils
import java.awt.image.BufferedImage
import java.io.File
import java.nio.ByteOrder
import java.util.*
import javax.imageio.ImageIO

object ImageLoader {

    private val cache = WeakHashMap<String, Image>()

    @JvmOverloads
    fun load(path: String, flipY: Boolean = true): Image {

        val file = File(path)
        if (!file.exists()) {
            throw NoSuchFileException(file)
        }
        val isJpg = isJpeg(file)

        return cache.getOrPut(file.absolutePath) {

            var img = ImageIO.read(file)
            if (flipY) {
                img = createFlipped(img)
            }

            val res = img.width * img.height
            val pixels = IntArray(res)
            img.getRGB(0, 0, img.width, img.height, pixels, 0, img.width)

            val buffer = BufferUtils.createByteBuffer(pixels.size * (if (isJpg) 3 else 4))
            buffer.order(ByteOrder.nativeOrder())
            for (y in 0 until img.height) {
                for (x in 0 until img.width) {
                    val pixel = pixels[y * img.width + x]
                    buffer.put((pixel shr 16 and 0xFF).toByte()) // Red component
                    buffer.put((pixel shr 8 and 0xFF).toByte()) // Green component
                    buffer.put((pixel and 0xFF).toByte()) // Blue component

                    if (!isJpg) {
                        buffer.put((pixel shr 24 and 0xFF).toByte()) // Alpha component. Only for RGBA
                    }
                }
            }
            buffer.rewind()

            Image(img.width, img.height, buffer)
        }
    }

    private fun isJpeg(file: File): Boolean {
        return file.name.endsWith(".jpg", true) || file.name.endsWith(".jpeg", true)
    }

    private fun createFlipped(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height
        val flipped = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = image.getRGB(x, y)
                flipped.setRGB(x, height - y - 1, pixel)
            }
        }
        return flipped
    }

}
