package Data;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageLoader {

    private static final Map<String, BufferedImage> cache = new ConcurrentHashMap<>();

    public static Optional<BufferedImage> loadImageFromResources(String resourcePath) {
        String cacheKey = "res:" + resourcePath;
        if (cache.containsKey(cacheKey)) {
            return Optional.of(cache.get(cacheKey));
        }

        try (InputStream stream = ImageLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                System.err.println("Ressource nicht gefunden: " + resourcePath);
                return Optional.empty();
            }

            // We need to read the stream into memory to read it twice (ImageIO and MetadataReader)
            byte[] data = toByteArray(stream);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            if (image == null) {
                return Optional.empty();
            }

            int orientation = getOrientation(new ByteArrayInputStream(data));
            image = rotateImage(image, orientation);

            cache.put(cacheKey, image);
            return Optional.of(image);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Ressource '" + resourcePath + "': " + e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<BufferedImage> loadImageFromFile(String filePath) {
        String cacheKey = "file:" + filePath;
        if (cache.containsKey(cacheKey)) {
            return Optional.of(cache.get(cacheKey));
        }

        try {
            File imageFile = new File(filePath);
            if (!imageFile.exists() || !imageFile.isFile()) {
                System.err.println("Datei nicht gefunden oder ist keine Datei: " + filePath);
                return Optional.empty();
            }

            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                return Optional.empty();
            }

            int orientation = getOrientation(imageFile);
            image = rotateImage(image, orientation);

            cache.put(cacheKey, image);
            return Optional.of(image);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei '" + filePath + "': " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void copyImageToUserImagesFolder(File file) {
        try  {
            Files.copy(file.toPath(), JsonUtil.getUserImagesPath().resolve(file.getName()));
        } catch (IOException e) {
            System.out.println("Could not copy image to user_images folder");
            e.printStackTrace();
        }
    }

    private static int getOrientation(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        } catch (Exception e) {
            // Ignore, default to orientation 1
        }
        return 1;
    }

    private static int getOrientation(InputStream stream) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(stream);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        } catch (Exception e) {
            // Ignore, default to orientation 1
        }
        return 1;
    }

    private static BufferedImage rotateImage(BufferedImage image, int orientation) {
        if (orientation <= 1) {
            return image;
        }

        AffineTransform t = new AffineTransform();
        boolean swapWidthHeight = false;

        switch (orientation) {
            case 2: // Flip horizontal
                t.scale(-1.0, 1.0);
                t.translate(-image.getWidth(), 0);
                break;
            case 3: // Rotate 180
                t.translate(image.getWidth(), image.getHeight());
                t.rotate(Math.PI);
                break;
            case 4: // Flip vertical
                t.scale(1.0, -1.0);
                t.translate(0, -image.getHeight());
                break;
            case 5: // Transpose
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                swapWidthHeight = true;
                break;
            case 6: // Rotate 90 CW
                t.translate(image.getHeight(), 0);
                t.rotate(Math.PI / 2);
                swapWidthHeight = true;
                break;
            case 7: // Transverse
                t.rotate(Math.PI / 2);
                t.scale(-1.0, 1.0);
                swapWidthHeight = true;
                break;
            case 8: // Rotate 270 CW
                t.translate(0, image.getWidth());
                t.rotate(3 * Math.PI / 2);
                swapWidthHeight = true;
                break;
            default:
                return image;
        }

        AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);
        int type = image.getType();
        if (type == BufferedImage.TYPE_CUSTOM || type == 0) {
            type = image.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        }
        
        BufferedImage destination = new BufferedImage(
                swapWidthHeight ? image.getHeight() : image.getWidth(),
                swapWidthHeight ? image.getWidth() : image.getHeight(),
                type
        );
        return op.filter(image, destination);
    }

    private static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }
}
