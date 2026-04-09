package miet.server.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileManager {
    private static final HashMap<String, Path> fileTable = new HashMap<>();

    public static String storeFile(String originalName, byte[] data) {
        String fid = String.valueOf(System.currentTimeMillis());
        Path path;

        try {
            path = Paths.get("uploads/" + originalName);
            Files.createDirectory(Path.of("uploads"));
            Files.createFile(path);
            Files.write(path, data);
        } catch (IOException e) {
            return String.valueOf(-1);
        }

        fileTable.put(fid, path);
        return fid;
    }

    public static Path getFile(String fid){
        return fileTable.get(fid);
    }
}
