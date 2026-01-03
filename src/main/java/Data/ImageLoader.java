package Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

        try {
            // Holt den InputStream der Ressource über den ClassLoader
            InputStream stream = ImageLoader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (stream == null) {
                System.err.println("Ressource nicht gefunden: " + resourcePath);
                return Optional.empty();
            }
            // ImageIO liest die Bilddaten aus dem Stream
            BufferedImage image = ImageIO.read(stream);
            if (image != null) {
                cache.put(cacheKey, image);
            }
            return Optional.ofNullable(image);
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
            // Erstellt ein File-Objekt aus dem Pfad
            File imageFile = new File(filePath);
            if (!imageFile.exists() || !imageFile.isFile()) {
                System.err.println("Datei nicht gefunden oder ist keine Datei: " + filePath);
                return Optional.empty();
            }
            // ImageIO liest die Bilddaten aus der Datei
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                cache.put(cacheKey, image);
            }
            return Optional.ofNullable(image);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei '" + filePath + "': " + e.getMessage());
            return Optional.empty();
        }
    }
}