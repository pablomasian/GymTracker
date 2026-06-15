package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "badge")
public class Badge {
    
    
    public enum BadgeType {
		HUNDRED,
        FIFTY_WORKOUTS,
        EARLY_BIRD,
        VOLUME_KING,
        CONSISTENCY_CHAMPION,
        OTHER
	}

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_badge")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime date;
    private String iconUrl;
    
    @Enumerated(EnumType.STRING)
    private BadgeType type;

    private String description;

    

    public Badge() {
    }
    

    public Badge(User user, LocalDateTime date,BadgeType type) {
        this.user = user;
        this.date = date;
        this.iconUrl = defaultIconForType(type);
        this.type = type; 
        this.description = defaultDescriptionForType(type);       
    }


    private String defaultIconForType(BadgeType type) {
    return switch (type) {
        case HUNDRED -> "/img/badges/hundred.png";
        case FIFTY_WORKOUTS -> "/img/badges/fifty_workouts.png";
        case EARLY_BIRD -> "/img/badges/early_bird.png";
        case VOLUME_KING -> "/img/badges/volume_king.png";
        case CONSISTENCY_CHAMPION -> "/img/badges/consistency_champion.png";
        case OTHER   -> "/img/badges/other.png";
    };
    }


    private String defaultDescriptionForType(BadgeType type) {
    return switch (type) {
        case HUNDRED -> "This user has completed a set using a weight of 100kg or more";
        case FIFTY_WORKOUTS -> "This user has completed 50 workouts";
        case EARLY_BIRD -> "This user has trained before 7:00 AM";
        case VOLUME_KING -> "This user has completed 200 or more reps in a single workout";
        case CONSISTENCY_CHAMPION -> "This user has trained for 7 consecutive days";
        case OTHER   -> "/img/badges/other.png";
    };
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


    public BadgeType getType() {
        return type;
    }


    public void setType(BadgeType type) {
        this.type = type;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getIconUrl() {
        return iconUrl;
    }


    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    

    




}
