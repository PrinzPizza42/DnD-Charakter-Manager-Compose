package Data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtil {
    private static final JsonUtil instance = new JsonUtil();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Path dataPath;

    private JsonUtil() {
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".DnD-Character-Manager");
            if(!path.toFile().exists()){
                System.out.println(".DnD-Character-Manager does not exist");
                Files.createDirectories(path);
            }
            System.out.println(".DnD-Character-Manager exists already");
            this.dataPath = path;
        }
        catch (Exception e){
            System.out.println("could not check for .DnD-Character-Manager");
            e.printStackTrace();
        }
    }

    private JsonUtil getInstance(){
        return instance;
    }

    public static Path getDataPath(){
        return dataPath;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}