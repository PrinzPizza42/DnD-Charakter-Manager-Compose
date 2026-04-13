package disk

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.*
import java.nio.file.Files
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

object ImageLoader {
    private val cache = ConcurrentHashMap<String, BufferedImage>()

    fun loadImageFromResources(resourcePath: String): Optional<BufferedImage> {
        val cacheKey = "res:$resourcePath"
        cache[cacheKey]?.let { return Optional.of(it) }

        try {
            val stream = ImageLoader::class.java.classLoader.getResourceAsStream(resourcePath)
            if (stream == null) {
                System.err.println("Ressource nicht gefunden: $resourcePath")
                return Optional.empty()
            }

            val data = toByteArray(stream)
            var image = ImageIO.read(ByteArrayInputStream(data)) ?: return Optional.empty()

            val orientation = getOrientation(ByteArrayInputStream(data))
            image = rotateImage(image, orientation)

            cache[cacheKey] = image
            return Optional.of(image)
        } catch (e: IOException) {
            System.err.println("Fehler beim Laden der Ressource '$resourcePath': ${e.message}")
            return Optional.empty()
        }
    }

    fun loadImageFromFile(filePath: String): Optional<BufferedImage> {
        val cacheKey = "file:$filePath"
        cache[cacheKey]?.let { return Optional.of(it) }

        try {
            val imageFile = File(filePath)
            if (!imageFile.exists() || !imageFile.isFile) {
                System.err.println("Datei nicht gefunden oder ist keine Datei: $filePath")
                return Optional.empty()
            }

            var image = ImageIO.read(imageFile) ?: return Optional.empty()

            val orientation = getOrientation(imageFile)
            image = rotateImage(image, orientation)

            cache[cacheKey] = image
            return Optional.of(image)
        } catch (e: IOException) {
            System.err.println("Fehler beim Laden der Datei '$filePath': ${e.message}")
            return Optional.empty()
        }
    }

    fun copyImageToUserImagesFolder(file: File, finalFileName: String) {
        try {
            Files.copy(file.toPath(), JsonUtil.userImagesPath.resolve(finalFileName))
        } catch (e: IOException) {
            println("Could not copy image to user_images folder")
            e.printStackTrace()
        }
    }

    private fun getOrientation(file: File): Int {
        try {
            val metadata = ImageMetadataReader.readMetadata(file)
            val directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION)
            }
        } catch (e: Exception) {
            // Ignore, default to 1
        }
        return 1
    }

    private fun getOrientation(stream: InputStream): Int {
        try {
            val metadata = ImageMetadataReader.readMetadata(stream)
            val directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION)
            }
        } catch (e: Exception) {
            // Ignore, default to 1
        }
        return 1
    }

    private fun rotateImage(image: BufferedImage, orientation: Int): BufferedImage {
        if (orientation <= 1) return image

        val t = AffineTransform()
        var swapWidthHeight = false

        when (orientation) {
            2 -> { // Flip horizontal
                t.scale(-1.0, 1.0)
                t.translate(-image.width.toDouble(), 0.0)
            }
            3 -> { // Rotate 180
                t.translate(image.width.toDouble(), image.height.toDouble())
                t.rotate(Math.PI)
            }
            4 -> { // Flip vertical
                t.scale(1.0, -1.0)
                t.translate(0.0, -image.height.toDouble())
            }
            5 -> { // Transpose
                t.rotate(-Math.PI / 2)
                t.scale(-1.0, 1.0)
                swapWidthHeight = true
            }
            6 -> { // Rotate 90 CW
                t.translate(image.height.toDouble(), 0.0)
                t.rotate(Math.PI / 2)
                swapWidthHeight = true
            }
            7 -> { // Transverse
                t.rotate(Math.PI / 2)
                t.scale(-1.0, 1.0)
                swapWidthHeight = true
            }
            8 -> { // Rotate 270 CW
                t.translate(0.0, image.width.toDouble())
                t.rotate(3 * Math.PI / 2)
                swapWidthHeight = true
            }
            else -> return image
        }

        val op = AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR)
        var type = image.type
        if (type == BufferedImage.TYPE_CUSTOM || type == 0) {
            type = if (image.colorModel.hasAlpha()) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
        }

        val destination = BufferedImage(
            if (swapWidthHeight) image.height else image.width,
            if (swapWidthHeight) image.width else image.height,
            type
        )
        return op.filter(image, destination)
    }

    private fun toByteArray(inputStream: InputStream): ByteArray {
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } != -1) {
            baos.write(buffer, 0, length)
        }
        return baos.toByteArray()
    }
}
