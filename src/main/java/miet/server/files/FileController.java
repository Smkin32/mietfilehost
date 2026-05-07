package miet.server.files;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/v1/files")
public class FileController {

    private final MinioService minioService;
    private final FileInfoRepository fileInfoRepository;
    private final UserRepository userRepository;

    public FileController(MinioService minioService,
                          FileInfoRepository fileInfoRepository,
                          UserRepository userRepository) {
        this.minioService = minioService;
        this.fileInfoRepository = fileInfoRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл пуст");
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            String fid = UUID.randomUUID().toString().replace("-", "");

            String bucketName = "user-" + user.getId();
            String originalName = file.getOriginalFilename();
            String objectName = fid + "_" + originalName;

            minioService.uploadFile(bucketName, objectName,
                    file.getBytes(), file.getContentType());

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFid(fid);
            fileInfo.setOwner(user);
            fileInfo.setOriginalName(originalName);
            fileInfo.setBucketName(bucketName);
            fileInfo.setObjectName(objectName);
            fileInfo.setSize(file.getSize());
            fileInfoRepository.save(fileInfo);

            return ResponseEntity.ok(fid);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка загрузки файла");
        }
    }

    @GetMapping("/download/{fid}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fid) {
        FileInfo fileInfo = fileInfoRepository.findByFid(fid)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        if (!fileInfo.getOwner().getUsername().equals(currentUsername) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        byte[] data = minioService.downloadFile(
                fileInfo.getBucketName(), fileInfo.getObjectName());

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getOriginalName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}