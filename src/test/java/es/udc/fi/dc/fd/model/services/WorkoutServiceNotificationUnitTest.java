package es.udc.fi.dc.fd.model.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Notification;
import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.rest.dtos.LogSetDto;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.RankingEntryDto;
import es.udc.fi.dc.fd.model.services.BadgeService;
import es.udc.fi.dc.fd.model.services.FollowService;
import es.udc.fi.dc.fd.model.services.PermissionChecker;
import es.udc.fi.dc.fd.model.services.StreakService;

public class WorkoutServiceNotificationUnitTest {

    @Test
    public void finishWorkout_createsNotification_whenUserRemovedFromTop3() throws Exception {
        // Arrange
        WorkoutServiceImpl service = spy(new WorkoutServiceImpl());

        PermissionChecker permissionChecker = mock(PermissionChecker.class);
        WorkoutSessionDao sessionDao = mock(WorkoutSessionDao.class);
        SetLogDao setLogDao = mock(SetLogDao.class);
        ExerciseDao exerciseDao = mock(ExerciseDao.class);
        UserDao userDao = mock(UserDao.class);
        FollowService followService = mock(FollowService.class);
        NotificationDao notificationDao = mock(NotificationDao.class);
        BadgeService badgeService = mock(BadgeService.class);
        StreakService streakService = mock(StreakService.class);

        // inject mocks via reflection
        java.lang.reflect.Field f;
        f = WorkoutServiceImpl.class.getDeclaredField("permissionChecker"); f.setAccessible(true); f.set(service, permissionChecker);
        f = WorkoutServiceImpl.class.getDeclaredField("workoutSessionDao"); f.setAccessible(true); f.set(service, sessionDao);
        f = WorkoutServiceImpl.class.getDeclaredField("setLogDao"); f.setAccessible(true); f.set(service, setLogDao);
        f = WorkoutServiceImpl.class.getDeclaredField("exerciseDao"); f.setAccessible(true); f.set(service, exerciseDao);
        f = WorkoutServiceImpl.class.getDeclaredField("userDao"); f.setAccessible(true); f.set(service, userDao);
        f = WorkoutServiceImpl.class.getDeclaredField("followService"); f.setAccessible(true); f.set(service, followService);
        f = WorkoutServiceImpl.class.getDeclaredField("notificationDao"); f.setAccessible(true); f.set(service, notificationDao);
        f = WorkoutServiceImpl.class.getDeclaredField("badgeService"); f.setAccessible(true); f.set(service, badgeService);
        f = WorkoutServiceImpl.class.getDeclaredField("streakService"); f.setAccessible(true); f.set(service, streakService);

        Long actorId = 10L;
        Long followerId = 20L;
        Long exerciseId = 100L;

        User actor = new User(); actor.setId(actorId); actor.setUsername("actor"); actor.setNombreUsuario("ActorName");
        User follower = new User(); follower.setId(followerId); follower.setUsername("victim"); follower.setNombreUsuario("VictimName");
        // ensure numeric fields are non-null to avoid NPEs in service
        actor.setStreakCount(0);
        follower.setStreakCount(0);

        WorkoutSession session = new WorkoutSession(); session.setId(1L); session.setUser(actor); session.setStartTime(LocalDateTime.now());

        // permission check
        doReturn(actor).when(permissionChecker).checkUser(actorId);
        doReturn(java.util.Optional.of(session)).when(sessionDao).findById(1L);

        // follow service returns one follower (the one who will be removed)
        doReturn(List.of(follower)).when(followService).getFollowersList(actorId);

        // exercise exists
        Exercise ex = new Exercise(); ex.setId(exerciseId); ex.setName("TestEx");
        doReturn(java.util.Optional.of(ex)).when(exerciseDao).findById(exerciseId);
        doReturn(true).when(exerciseDao).existsById(exerciseId);

        // userDao returns users
        doReturn(java.util.Optional.of(actor)).when(userDao).findById(actorId);
        doReturn(java.util.Optional.of(follower)).when(userDao).findById(followerId);

        // Spy getExerciseRanking: first call (previous) includes follower, second call (new) excludes follower
        RankingEntryDto old1 = new RankingEntryDto(followerId, "VictimName", new BigDecimal("50"));
        RankingEntryDto old2 = new RankingEntryDto(actorId, "ActorName", new BigDecimal("60"));
        RankingEntryDto new1 = new RankingEntryDto(actorId, "ActorName", new BigDecimal("60"));

        doReturn(List.of(old1, old2)).doReturn(List.of(new1)).when(service).getExerciseRanking(eq(followerId), eq(exerciseId));

        // Prepare DTO: one set for the exercise
        LogSetDto setDto = new LogSetDto(); setDto.setExerciseId(exerciseId); setDto.setReps(10); setDto.setWeight(new BigDecimal("60")); setDto.setSetNumber(1);
        LogWorkoutRequestDto dto = new LogWorkoutRequestDto(); dto.setSets(List.of(setDto)); dto.setRoutineId(1L); dto.setDate(LocalDateTime.now());

        // Act
        service.finishWorkout(actorId, 1L, dto);

        // Assert: notificationDao.save called at least once for the follower
        verify(notificationDao, times(1)).save(any(Notification.class));
    }
}
