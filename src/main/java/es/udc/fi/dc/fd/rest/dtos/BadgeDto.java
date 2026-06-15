package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDateTime;

import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;
import es.udc.fi.dc.fd.model.entities.User;

public class BadgeDto {
    private long id;    
    private User user;
    private LocalDateTime date;
    private String iconUrl;
    private String type;
    private String description;

    

    public BadgeDto() {
    }
    

    public BadgeDto(long id, User user, LocalDateTime date,BadgeType type, String description, String iconUrl) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.iconUrl = iconUrl;
        this.type = type.toString(); 
        this.description = description;       
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


    public String getIconUrl() {
        return iconUrl;
    }


    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    

}




    



    







