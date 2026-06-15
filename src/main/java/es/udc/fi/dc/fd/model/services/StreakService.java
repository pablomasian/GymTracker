package es.udc.fi.dc.fd.model.services;

import java.time.LocalDate;

public interface StreakService {

    void registrarEntrenamiento(Long userId);

    void generarAvisosFinDeRacha();

    void registrarEntrenamientoEnFecha(Long userId, LocalDate date);

}
