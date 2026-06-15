package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDateTime;

public class FollowerNotificationDto {
    
    private Long id;
    private Long coachId;
    private String followerUsername;
    private String followerFirstName;
    private String followerLastName;
    private LocalDateTime createdAt;
    private boolean isRead;
    
    public FollowerNotificationDto() {}
    
    public FollowerNotificationDto(Long id, Long coachId, String followerUsername, 
                                   String followerFirstName, String followerLastName,
                                   LocalDateTime createdAt, boolean isRead) {
        this.id = id;
        this.coachId = coachId;
        this.followerUsername = followerUsername;
        this.followerFirstName = followerFirstName;
        this.followerLastName = followerLastName;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public String getFollowerUsername() {
        return followerUsername;
    }

    public void setFollowerUsername(String followerUsername) {
        this.followerUsername = followerUsername;
    }

    public String getFollowerFirstName() {
        return followerFirstName;
    }

    public void setFollowerFirstName(String followerFirstName) {
        this.followerFirstName = followerFirstName;
    }

    public String getFollowerLastName() {
        return followerLastName;
    }

    public void setFollowerLastName(String followerLastName) {
        this.followerLastName = followerLastName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
