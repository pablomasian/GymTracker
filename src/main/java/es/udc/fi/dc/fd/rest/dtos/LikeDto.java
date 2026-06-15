package es.udc.fi.dc.fd.rest.dtos;

import java.time.Instant;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class LikeDto {
    
    private Long id;
    private Long liker_id;
    private String liker_username;
    private Long liked_id;
    private String liked_username;
    private Long sessionId;
    private String routineName;
    private Instant createdAt;
    private boolean isRead;

    public LikeDto (Long id, User liker, User liked, WorkoutSession session, Instant createdAt) {
        this.id = id;
        this.liker_id = liker.getId();
        this.liker_username = liker.getNombreUsuario();
        this.liked_id = liked.getId();
        this.liked_username = liked.getNombreUsuario();
        this.sessionId = session.getId();
        this.routineName = session.getRoutine().getName();
        this.createdAt = createdAt;
        this.isRead = false;
    }

    

    public LikeDto() {
    }



    public Long getId() {
        return id;
    }



    public Long getLiker_id() {
        return liker_id;
    }



    public String getLiker_username() {
        return liker_username;
    }



    public Long getSessionId() {
        return sessionId;
    }



    public String getRoutineName() {
        return routineName;
    }



    public Instant getCreatedAt() {
        return createdAt;
    }



    public void setLiker_id(Long liker_id) {
        this.liker_id = liker_id;
    }



    public void setLiker_username(String liker_username) {
        this.liker_username = liker_username;
    }



    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }



    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }



    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }



    public Long getLiked_id() {
        return liked_id;
    }



    public String getLiked_username() {
        return liked_username;
    }



    public void setLiked_id(Long liked_id) {
        this.liked_id = liked_id;
    }



    public void setLiked_username(String liked_username) {
        this.liked_username = liked_username;
    }



    public boolean isRead() {
        return isRead;
    }



    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }



    
    


    
    

}
