package es.udc.fi.dc.fd.rest.dtos;

public class FollowedCoachDto {

    private Long id;
    private String username;
    private String nombreUsuario;
    private String avatarUrl;
    private String role;

    public FollowedCoachDto() {}

    public FollowedCoachDto(Long id, String username, String nombreUsuario, String avatarUrl, String role) {
        this.id = id;
        this.username = username;
        this.nombreUsuario = nombreUsuario;
        this.avatarUrl = avatarUrl;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
