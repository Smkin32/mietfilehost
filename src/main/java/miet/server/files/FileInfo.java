package miet.server.files;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "files")
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String bucketName;

    @Column(nullable = false)
    private String objectName;

    private long size;

    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFid() { return fid; }
    public void setFid(String fid) { this.fid = fid; }
    public UserEntity getOwner() { return owner; }
    public void setOwner(UserEntity owner) { this.owner = owner; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }
    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}