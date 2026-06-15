package es.udc.fi.dc.fd.rest.dtos;

import java.time.Instant;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class CommentDto {
    
    private Long id;
    private Long commenter_id;
    private String commenter_username;
    private Long commented_id;
    private String commented_username;
    private Long session_id;
    private String routine_name;
    private String text;
    private Instant createdAt;
    private boolean isRead;


    public CommentDto(Long id,  WorkoutSession session, User commenter, User commented, String text, Instant createdAt) {
        this.id = id;
        this.commenter_id = commenter.getId();
        this.commenter_username = commenter.getNombreUsuario();
        this.commented_id = commented.getId();
        this.commented_username = commented.getNombreUsuario();
        this.session_id = session.getId();
        this.routine_name = session.getRoutine().getName();
        this.text = text;
        this.createdAt = createdAt;
        this.isRead = false;
    }




    public Long getCommenter_id() {
        return commenter_id;
    }


    public String getCommenter_username() {
        return commenter_username;
    }


    public Long getCommented_id() {
        return commented_id;
    }


    public String getCommented_username() {
        return commented_username;
    }



    public String getText() {
        return text;
    }


    public Instant getCreatedAt() {
        return createdAt;
    }


    public void setCommenter_id(Long commenter_id) {
        this.commenter_id = commenter_id;
    }


    public void setCommenter_username(String commenter_username) {
        this.commenter_username = commenter_username;
    }


    public void setCommented_id(Long commented_id) {
        this.commented_id = commented_id;
    }


    public void setCommented_username(String commented_username) {
        this.commented_username = commented_username;
    }


    


    public void setSession_id(Long session_id) {
        this.session_id = session_id;
    }


    public void setRoutine_name(String routine_name) {
        this.routine_name = routine_name;
    }


    public Long getSession_id() {
        return session_id;
    }


    public String getRoutine_name() {
        return routine_name;
    }


    public void setText(String text) {
        this.text = text;
    }


    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }


    public boolean isRead() {
        return isRead;
    }


    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    

    


    


}
