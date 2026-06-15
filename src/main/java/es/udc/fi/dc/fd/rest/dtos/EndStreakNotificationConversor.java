package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;

import java.util.List;
import java.util.stream.Collectors;

public class EndStreakNotificationConversor {

    public static EndStreakNotificationDto toDto(NotificationEndStreak n) {
        return new EndStreakNotificationDto(
                n.getId(),
                n.getDiasRacha(),
                n.getMensaje(),
                n.getFechaLimite(),
                n.getFechaCreacion(),
                n.isLeido()
        );
    }

    public static List<EndStreakNotificationDto> toDtos(List<NotificationEndStreak> notifications) {
        return notifications.stream().map(EndStreakNotificationConversor::toDto).collect(Collectors.toList());
    }
}
