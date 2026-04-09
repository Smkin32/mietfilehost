package miet.server.files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/v1/files")
public class FileController {

    @GetMapping("/download/{fid}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fid) {
        try {
            Path filePath = FileManager.getFile(fid);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл пуст");
            }

            String originalName = file.getOriginalFilename();

            byte[] bytes = file.getBytes();

            String fid = FileManager.storeFile(originalName, bytes);

            return ResponseEntity.ok(fid);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка загрузки");
        }
    }

}