package es.udc.fi.dc.fd.model.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StreakCron {

    private final StreakService streakService;

    public StreakCron(StreakService streakService) {
        this.streakService = streakService;
    }

    /**
     * Ejecuta cada día a las 20:00
     * Cron expresión: Segundos Minutos Hora DíaMes Mes DíaSemana
     * 0 0 20 * * *  →  20:00:00 todos los días
     */
    //@Scheduled(cron = "0 * * * * *") // Para pruebas: cada minuto
    @Scheduled(cron = "0 0 20 * * *")
    public void ejecutarAvisosFinDeRacha() {
        streakService.generarAvisosFinDeRacha();
    }
}
