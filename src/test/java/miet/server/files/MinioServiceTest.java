package miet.server.files;

import io.minio.*;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    @Test
    void ensureUserBucket_ShouldCreateBucket_WhenNotExists() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        minioService.ensureUserBucket("user-1");

        verify(minioClient, times(1)).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void ensureUserBucket_ShouldNotCreateBucket_WhenExists() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        minioService.ensureUserBucket("user-1");

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void uploadFile_ShouldCallPutObject() throws Exception {
        byte[] data = "test data".getBytes();

        minioService.uploadFile("user-1", "file.txt", data, "text/plain");

        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void downloadFile_ShouldReturnFileData() throws Exception {
        byte[] expectedData = "downloaded data".getBytes();
        GetObjectResponse response = mock(GetObjectResponse.class);
        when(response.readAllBytes()).thenReturn(expectedData);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        byte[] actualData = minioService.downloadFile("user-1", "file.txt");

        assertArrayEquals(expectedData, actualData);
    }

    @Test
    void deleteFile_ShouldCallRemoveObject() throws Exception {
        minioService.deleteFile("user-1", "file.txt");

        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }
}