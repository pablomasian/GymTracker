package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.FollowerNotification;

import java.util.List;
import java.util.stream.Collectors;

public class FollowerNotificationConversor {
    
    public static FollowerNotificationDto toFollowerNotificationDto(FollowerNotification notification) {
        return new FollowerNotificationDto(
            notification.getId(),
            notification.getCoach().getId(),
            notification.getFollower().getUsername(),
            notification.getFollower().getFirstName(),
            notification.getFollower().getLastName(),
            notification.getCreatedAt(),
            notification.isRead()
        );
    }
    
    public static List<FollowerNotificationDto> toFollowerNotificationDtos(List<FollowerNotification> notifications) {
        return notifications.stream()
            .map(FollowerNotificationConversor::toFollowerNotificationDto)
            .collect(Collectors.toList());
    }
}
