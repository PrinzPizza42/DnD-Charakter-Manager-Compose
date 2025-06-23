package Data;

import Main.Inventory;
import Main.Main;
import androidx.compose.runtime.snapshots.SnapshotStateList;
import main.InvSelector;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Read {
    private static final Read instance = new Read();

    private Read() {
    }

    public static Read getInstance() {
        return instance;
    }

    public static void readData() {
        if(checkIfPDFIsThere()) {
            Main.usePDF = true;
            readPDF();
        }
        else readJSON();
    }

    private static void readJSON() {
        System.out.println("Reading JSON");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(JsonUtil.getDataPath(), "*.json")) {
            SnapshotStateList<Inventory> preInvs = new SnapshotStateList<>();
            for (Path path : stream) {
                try {
                    Inventory inv = JsonUtil.getMapper().readValue(path.toFile(), Inventory.class);
                    preInvs.add(inv);
                } catch (Exception e) {
                    System.err.println("Fehler beim Laden von Datei " + path.getFileName() + ": " + e.getMessage());
                }
            }
            InvSelector.INSTANCE.setInventoryMutableList(preInvs);
        } catch (Exception e) {
            System.err.println("Konnte Verzeichnis nicht lesen: " + e.getMessage());
        }
    }

    private static boolean checkIfPDFIsThere() {
        return false; //TODO implement checking in same folder
    }

    private static void readPDF() {
        System.out.println("Reading pdf"); //TODO implement reading pdf
    }
}
