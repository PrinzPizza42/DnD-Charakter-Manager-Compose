package Data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtil {
    private static final JsonUtil instance = new JsonUtil();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Path dataPath;
    private static Path userImagesPath;

    private JsonUtil() {
        try {
            Path dataPath = Paths.get(System.getProperty("user.home"), ".DnD-Character-Manager");
            if(!dataPath.toFile().exists()){
                Files.createDirectories(dataPath);
                System.out.println("Created data dir: " + dataPath.toAbsolutePath());
            }
            System.out.println(".DnD-Character-Manager exists already");
            JsonUtil.dataPath = dataPath;

            Path userImagesPath = JsonUtil.dataPath.resolve("user_images");
            if(!userImagesPath.toFile().exists()) {
                Files.createDirectories(userImagesPath);
                System.out.println("Created user image dir: " + userImagesPath.toAbsolutePath());
            }
            JsonUtil.userImagesPath = userImagesPath;
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

    public static Path getUserImagesPathPath(){
        return userImagesPath;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}