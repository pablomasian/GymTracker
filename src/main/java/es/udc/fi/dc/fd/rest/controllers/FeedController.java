package es.udc.fi.dc.fd.rest.controllers;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.SetLog;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.services.FollowService;
import es.udc.fi.dc.fd.model.services.SetLogService;
import es.udc.fi.dc.fd.model.services.SocialService;
import es.udc.fi.dc.fd.model.services.UserService;
import es.udc.fi.dc.fd.model.services.WorkoutService;
import es.udc.fi.dc.fd.model.services.BlockService;
import es.udc.fi.dc.fd.rest.dtos.*;

import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/feed")
public class FeedController {


@Autowired 
private WorkoutService workoutService;
@Autowired
private FollowService followService;
@Autowired
private UserService userService;
@Autowired
private SocialService socialService;
@Autowired
private SetLogService setLogService;
@Autowired
private WorkoutSessionConversor sessionConversor;
@Autowired
private UserDao userDao;
@Autowired
private BlockService blockService;

@GetMapping("/workouts")
public List<WorkoutSessionDto> getOwnAndFriendsWorkouts(@RequestAttribute Long userId) throws InstanceNotFoundException{
    
    User self = userDao.getById(userId);

    List<User> followedUsersList = followService.allFollowed(userId);

    followedUsersList.add(self);

    List<WorkoutSession> allSessions = new ArrayList<>();
    
    for (User followedUser : followedUsersList) {
        if (blockService.isBlocked(userId, followedUser.getId()) || 
            blockService.hasBlockedMe(userId, followedUser.getId())) {
            continue;
        }
        List<WorkoutSession> userSessions = workoutService.getWorkoutSessionsByUser(followedUser.getId());
        allSessions.addAll(userSessions);
    }

    allSessions.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

    return sessionConversor.toWorkoutSessionDtosFeed(allSessions, userId);

}


@PostMapping("/{sessionId}/comment")
public void comment (@RequestAttribute Long userId, @PathVariable Long sessionId, @RequestParam String text ){


    socialService.comment(userId, sessionId, text);
}

@PostMapping("/like/{sessionId}")
public void like (@RequestAttribute Long userId, @PathVariable Long sessionId){
    
    

    socialService.like(userId, sessionId);

}

@PostMapping("/unlike/{sessionId}")
public void unlike (@RequestAttribute Long userId, @PathVariable Long sessionId){
    
    

    socialService.unlike(userId, sessionId);

}

@GetMapping("/session-feed-sets/{sessionId}")
public List<SetLogDto> getFeedWorkoutDetails(@PathVariable Long sessionId,
        HttpServletRequest request) throws AccessDeniedException {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null)
        throw new AccessDeniedException("User not authenticated");

    WorkoutSession session = workoutService.getWorkoutSessionById(sessionId);

    boolean isOwner = session != null && session.getUser().getId().equals(userId);
    boolean isCoachOfRoutine = session != null && session.getRoutine().getUser().getId().equals(userId);
    boolean isFollower = session != null && followService.alreadyFollowing(userId, session.getUser().getId());
    

    if (!isOwner && !isCoachOfRoutine && !isFollower) {
        throw new AccessDeniedException("You arent following the user or this is not your session");
    }

    List<SetLog> sets = setLogService.getSetsOfSession(sessionId);
    return sets.stream().map(SetLogConversor::toSetLogDto).toList();
}

@GetMapping("session-feed-details/{sessionId}")
public WorkoutSessionDto getFeedWorkoutSetDetails(@PathVariable Long sessionId,
        HttpServletRequest request) throws AccessDeniedException {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null)
        throw new AccessDeniedException("User not authenticated");

    WorkoutSession session = workoutService.getWorkoutSessionById(sessionId);

    boolean isOwner = session != null && session.getUser().getId().equals(userId);
    boolean isCoachOfRoutine = session != null && session.getRoutine().getUser().getId().equals(userId);
    boolean isFollower = session != null && followService.alreadyFollowing(userId, session.getUser().getId());

    if (!isOwner && !isCoachOfRoutine && !isFollower) {
        throw new AccessDeniedException("You arent following the user or this is not your session");
    }

    WorkoutSessionDto sessionDto = sessionConversor.toWorkoutSessionDtoFeed(session, userId);

    return sessionDto;
}

@GetMapping("likes-count/{sessionId}")
public int getSessionLikeCount (@PathVariable Long sessionId){

    int count = socialService.getSessionLikesCount(sessionId);

    return count;
}

@GetMapping("session-likers/{sessionId}")
public List<User> getSessionLikers (@PathVariable Long sessionId) {

    return socialService.getSessionLikers(sessionId);
}

@GetMapping("comments-of-session/{sessionId}")
public List<CommentDto> getSessionComments (@PathVariable Long sessionId){
    return CommentConversor.toCommentDtos(socialService.getSessionComments(sessionId));
}





}