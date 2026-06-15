package es.udc.fi.dc.fd.rest.dtos;

public class UserPrivateDto {

    private Long id;
    private String nombreUsuario;
    private String username;
    private String email;
    private String avatarUrl;

    private Double weight;
    private Double height;
    private Integer age;
    private String gender;
    private Double bmi;

    public void setId(Long id) { this.id = id; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl;}
    public void setWeight(Double weight) { this.weight = weight; }
    public void setHeight(Double height) { this.height = height; }
    public void setAge(Integer age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBmi(Double bmi) { this.bmi = bmi; }

    public Long getId() {return id;}
    public String getNombreUsuario() {return nombreUsuario;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getAvatarUrl() {return avatarUrl;}
    public Double getWeight() {return weight;}
    public Double getHeight() {return height;}
    public Integer getAge() {return age;}
    public String getGender() {return gender;}
    public Double getBmi() {return bmi;}

}
