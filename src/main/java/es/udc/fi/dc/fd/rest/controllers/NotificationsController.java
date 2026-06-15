package es.udc.fi.dc.fd.rest.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.FollowerNotificationService;
import es.udc.fi.dc.fd.model.services.NotificationsFollowedCoachService;
import es.udc.fi.dc.fd.model.services.SocialService;
import es.udc.fi.dc.fd.model.services.UserService;
import es.udc.fi.dc.fd.rest.dtos.CommentConversor;
import es.udc.fi.dc.fd.rest.dtos.CommentDto;
import es.udc.fi.dc.fd.rest.dtos.FollowerNotificationConversor;
import es.udc.fi.dc.fd.rest.dtos.FollowerNotificationDto;
import es.udc.fi.dc.fd.rest.dtos.LikeConversor;
import es.udc.fi.dc.fd.rest.dtos.LikeDto;
import es.udc.fi.dc.fd.rest.dtos.NotificationFollowedCoachConversor;
import es.udc.fi.dc.fd.rest.dtos.NotificationFollowedCoachDto;

import es.udc.fi.dc.fd.model.services.EndStreakNotificationService;
import es.udc.fi.dc.fd.rest.dtos.EndStreakNotificationConversor;
import es.udc.fi.dc.fd.rest.dtos.EndStreakNotificationDto;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    @Autowired private NotificationDao notificationDao;
    @Autowired private UserService userService;
    @Autowired private NotificationsFollowedCoachService notificationsFollowedCoachService;
    @Autowired private FollowerNotificationService followerNotificationService;
    @Autowired private SocialService socialService;
    @Autowired private LikeDao likeDao;
    @Autowired private CommentDao commentDao;
    @Autowired private EndStreakNotificationService endStreakNotificationService;

    @GetMapping
    public List<Map<String,Object>> myNotifications(@RequestAttribute Long userId) throws Exception {
        User me = userService.loginFromId(userId);
        return notificationDao.findByRecipientOrderByCreatedAtDesc(me).stream().map(n -> {
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", n.getId());
            m.put("type", n.getType());
            m.put("message", n.getMessage());
            m.put("read", n.isRead());
            m.put("createdAt", n.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@RequestAttribute Long userId, @PathVariable Long id) throws Exception {
        User me = userService.loginFromId(userId);
        notificationDao.findById(id).ifPresent(n -> {
            if (n.getRecipient().getId() == me.getId()) {
                n.setRead(true);
                notificationDao.save(n);
            }
        });
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@RequestAttribute Long userId) throws Exception {
        User me = userService.loginFromId(userId);
        long unreadCount = notificationDao.findByRecipientOrderByCreatedAtDesc(me).stream()
            .filter(n -> !n.isRead())
            .count();
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("count", unreadCount);
        return result;
    }

    @GetMapping("/streak")
    public List<EndStreakNotificationDto> getStreakNotifications(@RequestAttribute Long userId) {
        return EndStreakNotificationConversor.toDtos(
            endStreakNotificationService.getNotificationsForUser(userId)
        );
    }

    @GetMapping("/streak/unread-count")
    public Map<String, Long> getStreakUnreadCount(@RequestAttribute Long userId) {
        long count = endStreakNotificationService.countUnreadForUser(userId);
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("count", count);
        return result;
    }

    @PostMapping("/streak/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markStreakNotificationRead(@PathVariable Long id) {
        endStreakNotificationService.markAsRead(id);
    }

    @GetMapping("/followed-coach")
    public List<NotificationFollowedCoachDto> myNotificationsFollowedCoach(@RequestAttribute Long userId) throws Exception {
        return NotificationFollowedCoachConversor.toNotificationFollowedCoachDtos(
            notificationsFollowedCoachService.getMyNotifications(userId)
        );
    }

    @GetMapping("/followed-coach/unread-count")
    public Map<String, Long> getFollowedCoachUnreadCount(@RequestAttribute Long userId) throws Exception {
        long count = notificationsFollowedCoachService.countUnread(userId);
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("count", count);
        return result;
    }

    @PostMapping("/followed-coach/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markCoachRoutineNotificationRead(@RequestAttribute Long userId, @PathVariable Long id) throws Exception {
        notificationsFollowedCoachService.markAsRead(id);
    }

    @GetMapping("/followers")
    public List<FollowerNotificationDto> getFollowerNotifications(@RequestAttribute Long userId) throws Exception {
        return FollowerNotificationConversor.toFollowerNotificationDtos(
            followerNotificationService.getNotificationsForCoach(userId)
        );
    }

    @PostMapping("/followers/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markFollowerNotificationRead(@RequestAttribute Long userId, @PathVariable Long id) throws Exception {
        followerNotificationService.markAsRead(id);
    }

    @GetMapping("/followers/unread-count")
    public Map<String, Long> getFollowerNotificationsUnreadCount(@RequestAttribute Long userId) throws Exception {
        long count = followerNotificationService.countUnreadForCoach(userId);
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("count", count);
        return result;
    }

    @GetMapping("/my_comments")
    public List<CommentDto> getMyComments (@RequestAttribute Long userId){
        return CommentConversor.toCommentDtos(socialService.getMyComments(userId));
    }

    @GetMapping("/comments-unread-count")
    public Map<String, Long> getCommentsUnreadCount(@RequestAttribute Long userId) throws Exception {
        long unreadCount = commentDao.findAllByCommentedId(userId).stream()
            .filter(n -> !n.isRead())
            .count();
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("count", unreadCount);
        return result;
    }

    @PostMapping("/comments/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markCommentRead(@RequestAttribute Long userId, @PathVariable Long id) throws Exception {
        socialService.markCommentAsRead(id);
    }

    @GetMapping("/my_likes")
    public List<LikeDto> getMyLikes (@RequestAttribute Long userId){
        return LikeConversor.toLikeDtos(socialService.getMyLikes(userId));
    }

    @GetMapping("/likes-unread-count")
    public Map<String, Long> getLikesUnreadCount(@RequestAttribute Long userId) throws Exception {
        long unreadCount = likeDao.findAllByLikedId(userId).stream()
            .filter(n -> !n.isRead())
            .count();
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("count", unreadCount);
        return result;
    }

    @PostMapping("/likes/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markLikeRead(@RequestAttribute Long userId, @PathVariable Long id) throws Exception {
        socialService.markLikeAsRead(id);
    }

}
