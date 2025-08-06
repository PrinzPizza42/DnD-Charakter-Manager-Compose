package Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public final class ImageLoader {

    public static Optional<BufferedImage> loadImageFromResources(String resourcePath) {
        try {
            // Holt den InputStream der Ressource über den ClassLoader
            InputStream stream = ImageLoader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (stream == null) {
                System.err.println("Ressource nicht gefunden: " + resourcePath);
                return Optional.empty();
            }
            // ImageIO liest die Bilddaten aus dem Stream
            BufferedImage image = ImageIO.read(stream);
            return Optional.ofNullable(image);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Ressource '" + resourcePath + "': " + e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<BufferedImage> loadImageFromFile(String filePath) {
        try {
            // Erstellt ein File-Objekt aus dem Pfad
            File imageFile = new File(filePath);
            if (!imageFile.exists() || !imageFile.isFile()) {
                System.err.println("Datei nicht gefunden oder ist keine Datei: " + filePath);
                return Optional.empty();
            }
            // ImageIO liest die Bilddaten aus der Datei
            BufferedImage image = ImageIO.read(imageFile);
            return Optional.ofNullable(image);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei '" + filePath + "': " + e.getMessage());
            return Optional.empty();
        }
    }
}