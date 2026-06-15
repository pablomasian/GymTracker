package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDateTime;

public class EndStreakNotificationDto {

    private Long id;
    private int diasRacha;
    private String mensaje;
    private LocalDateTime fechaLimite;
    private LocalDateTime fechaCreacion;
    private boolean leido;

    public EndStreakNotificationDto(Long id, int diasRacha, String mensaje,
                                    LocalDateTime fechaLimite, LocalDateTime fechaCreacion,
                                    boolean leido) {
        this.id = id;
        this.diasRacha = diasRacha;
        this.mensaje = mensaje;
        this.fechaLimite = fechaLimite;
        this.fechaCreacion = fechaCreacion;
        this.leido = leido;
    }

    public Long getId() { return id; }
    public int getDiasRacha() { return diasRacha; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isLeido() { return leido; }
}
