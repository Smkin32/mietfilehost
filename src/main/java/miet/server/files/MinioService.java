package miet.server.files;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;

@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void ensureUserBucket(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания бакета: " + bucketName, e);
        }
    }

    public void uploadFile(String bucketName, String objectName,
                           byte[] data, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(data), Integer.toUnsignedLong(data.length), -1L)
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла в MinIO", e);
        }
    }

    public byte[] downloadFile(String bucketName, String objectName) {
        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {
            return response.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания файла из MinIO", e);
        }
    }

    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления файла из MinIO", e);
        }
    }
}