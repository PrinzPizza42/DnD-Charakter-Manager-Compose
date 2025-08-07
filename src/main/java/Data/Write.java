package Data;

import com.fasterxml.jackson.databind.ObjectMapper;
import Main.Inventory;
import Main.Main;
import main.InvSelector;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Write {
    private static final Write instance = new Write();

    private Write(){}

    public static Write getInstance() {
        return instance;
    }

    private static void saveInPDF() {
        System.out.println("in PDF");
    }

    private static void saveInJSON() {
        System.out.println("Saving in JSON");
        ObjectMapper mapper = JsonUtil.getMapper();
        for(Inventory inv : InvSelector.INSTANCE.getInventoryMutableList()) {
            safeInJSON(mapper, inv);
        }
    }

    private static void safeInJSON(ObjectMapper mapper, Inventory inv) {
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".DnD-Character-Manager", inv.getName() + ".json");
            File file = path.toFile();
            if(file.exists()) {
                System.out.println("File already exists at: " + file.getAbsolutePath());
                boolean delete = file.delete();
                System.out.println("Deleted: " + delete);
            }
            mapper.writeValue(new File(path.toString()), inv);
            System.out.println("Saved file");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("could not save inv" + inv.getName() + " into file");
        }
    }

    public static void safe(Inventory inv) {
        if(Main.usePDF) System.out.println("saving in pdf is in development");
        else safeInJSON(JsonUtil.getMapper(), inv);
    }

    public static void removeInv(Inventory inv) {
        try {
            Path path = JsonUtil.getDataPath().resolve(inv.getName() + ".json");
            if(path.toFile().exists()) Files.delete(path);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("could not delete inv" + inv.getName() + " from files");
        }
    }
}
