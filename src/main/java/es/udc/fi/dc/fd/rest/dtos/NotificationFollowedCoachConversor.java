package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoach;
import es.udc.fi.dc.fd.model.entities.User;

// Conversor entre NotificationFollowedCoach y su DTO
public class NotificationFollowedCoachConversor {

    private NotificationFollowedCoachConversor() {}

    // Conversor de entidad a DTO
    public static final NotificationFollowedCoachDto toNotificationFollowedCoachDto(NotificationFollowedCoach notification) {
        return new NotificationFollowedCoachDto(
            notification.getId(),
            notification.getRoutineName(),
            notification.getUser() != null ? notification.getUser().getId() : null,
            notification.getCoach() != null ? notification.getCoach().getId() : null,
            notification.getCoach() != null ? notification.getCoach().getUsername() : null,
            notification.isRead(),
            notification.getCreatedAt()
            // ajusta si usas otro getter
        );
    }

    // Conversor de lista de entidades a lista de DTOs
    public static final List<NotificationFollowedCoachDto> toNotificationFollowedCoachDtos(List<NotificationFollowedCoach> notifications) {
        return notifications.stream()
                .map(NotificationFollowedCoachConversor::toNotificationFollowedCoachDto)
                .collect(Collectors.toList());
    }

    // Conversor inverso opcional de DTO a entidad
    public static final NotificationFollowedCoach toNotificationFollowedCoach(NotificationFollowedCoachDto dto, User user, User coach) {
        NotificationFollowedCoach notification = new NotificationFollowedCoach(
            dto.getRoutineName(),
            user,
            coach
        );
        notification.setId(dto.getId());
        return notification;
    }
}
