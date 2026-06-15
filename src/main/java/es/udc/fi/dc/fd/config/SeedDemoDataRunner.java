package es.udc.fi.dc.fd.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.Comment;
import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.Follow;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.Like;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.StreakService;
import es.udc.fi.dc.fd.model.entities.SetLog;
import es.udc.fi.dc.fd.model.entities.SetLogDao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("!test")
public class SeedDemoDataRunner implements CommandLineRunner {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoutineDao routineDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private StreakService streakService;

    @Autowired
    private LikeDao likeDao;

    @Autowired
    private CommentDao commentDao;

    private static final String[] USER_COMMENTS = {
            "I tried this routine too, tough one!",
            "This one destroyed me 😅",
            "Looks challenging!",
            "Nice consistency!",
            "Hard session, respect 💪"
    };

    @Override
    public void run(String... args) throws Exception {

        // Usuarios demo con nombres reales
        ensureDemoUser("maria.garcia", "María García", "María", "García", "123456");
        ensureDemoUser("carlos.lopez", "Carlos López", "Carlos", "López", "123456");
        ensureDemoUser("laura.fernandez", "Laura Fernández", "Laura", "Fernández", "123456");

        // Coaches demo con nombres reales
        ensureDemoCoach("andres.sanchez", "Andrés Sánchez", "Andrés", "Sánchez", "123456");
        ensureDemoCoach("elena.martin", "Elena Martín", "Elena", "Martín", "123456");
        ensureDemoCoach("jorge.gomez", "Jorge Gómez", "Jorge", "Gómez", "123456");

        Routine r1 = getOrCreateRoutineForCoach("Full Body Beginner", "andres.sanchez");
        ensureRoutineExercises(r1,
                spec("Squat", 3, 8, bd(40)),
                spec("Bench Press", 3, 8, bd(30)),
                spec("Pull-up", 3, 6, null),
                spec("Plank", 3, 30, null));

        Routine r2 = getOrCreateRoutineForCoach("Upper Body Strength", "andres.sanchez");
        ensureRoutineExercises(r2,
                spec("Bench Press", 5, 5, bd(50)),
                spec("Overhead Press", 3, 5, bd(30)),
                spec("Pull-up", 4, 6, null),
                spec("Crunch", 3, 15, null));

        reassignRoutineToCoachIfNeeded("Quick Cardio", "elena.martin");
        Routine r3 = getOrCreateRoutineForCoach("Quick Cardio", "elena.martin");
        ensureRoutineExercises(r3,
                spec("Jump Rope", 5, 60, null),
                spec("Burpee", 4, 12, null),
                spec("Plank", 3, 45, null));

        reassignRoutineToCoachIfNeeded("Core Blast", "jorge.gomez");
        Routine r4 = getOrCreateRoutineForCoach("Core Blast", "jorge.gomez");
        ensureRoutineExercises(r4,
                spec("Plank", 4, 45, null),
                spec("Crunch", 4, 20, null),
                spec("Lunge", 3, 12, bd(10)));

        Routine r5 = getOrCreateRoutineForCoach("Leg Day Intense", "elena.martin");
        ensureRoutineExercises(r5,
                spec("Squat", 5, 5, bd(70)),
                spec("Deadlift", 3, 5, bd(90)),
                spec("Lunge", 3, 10, bd(12)));

        // --- Follow: maria.garcia sigue a carlos.lopez y laura.fernandez ---
        User user1 = userDao.findByUsername("maria.garcia").orElseThrow();
        User user2 = userDao.findByUsername("carlos.lopez").orElseThrow();
        User user3 = userDao.findByUsername("laura.fernandez").orElseThrow();

        if (!followDao.existsByFollowerIdAndCoachId(user1.getId(), user2.getId())) {
            followDao.save(new Follow(user1, user2));
        }
        if (!followDao.existsByFollowerIdAndCoachId(user1.getId(), user3.getId())) {
            followDao.save(new Follow(user1, user3));
        }

        ensureFollow("maria.garcia", "andres.sanchez");
        ensureFollow("maria.garcia", "elena.martin");

        ensureFollow("carlos.lopez", "andres.sanchez");
        ensureFollow("carlos.lopez", "jorge.gomez");

        ensureFollow("laura.fernandez", "elena.martin");

        ensureFollow("maria.garcia", "carlos.lopez");
        ensureFollow("maria.garcia", "laura.fernandez");

        ensureFollow("carlos.lopez", "laura.fernandez");
        ensureFollow("carlos.lopez", "maria.garcia");

        ensureFollow("andres.sanchez", "elena.martin");
        ensureFollow("elena.martin", "jorge.gomez");

        createWorkoutSession(
                "carlos.lopez",
                r1,
                LocalDateTime.now().minusDays(1).withHour(17),
                60,
                spec("Squat", 3, 12, bd(80.06)),
                spec("Bench Press", 3, 12, bd(80.06)),
                spec("Plank", 3, 12, null),
                spec("Crunch", 3, 12, null));
        createWorkoutSession(
                "maria.garcia",
                r1,
                LocalDateTime.now().minusDays(14).withHour(18),
                50,
                spec("Squat", 3, 8, bd(60)),
                spec("Bench Press", 3, 8, bd(45)),
                spec("Plank", 3, 30, null));

        createWorkoutSession(
                "maria.garcia",
                r2,
                LocalDateTime.now().minusDays(12).withHour(19),
                55,
                spec("Bench Press", 5, 5, bd(50)),
                spec("Pull-up", 4, 6, null),
                spec("Crunch", 3, 15, null));

        createWorkoutSession(
                "carlos.lopez",
                r3,
                LocalDateTime.now().minusDays(20).withHour(17),
                40,
                spec("Jump Rope", 5, 60, null),
                spec("Burpee", 4, 12, null));

        createWorkoutSession(
                "laura.fernandez",
                r4,
                LocalDateTime.now().minusDays(7).withHour(8),
                45,
                spec("Plank", 4, 45, null),
                spec("Crunch", 4, 20, null));

        createWorkoutSession(
                "laura.fernandez",
                r4,
                LocalDateTime.now().minusDays(6).withHour(8),
                45,
                spec("Plank", 4, 45, null),
                spec("Crunch", 4, 20, null));

        createWorkoutSession(
                "andres.sanchez",
                r1,
                LocalDateTime.now().minusDays(10).withHour(20),
                60,
                spec("Squat", 3, 8, bd(80)),
                spec("Bench Press", 3, 8, bd(60)));

        createWorkoutSession(
                "elena.martin",
                r3,
                LocalDateTime.now().minusDays(3).withHour(19),
                45,
                spec("Jump Rope", 4, 60, null),
                spec("Burpee", 3, 12, null));

        // =====================================================
        // RACHA ACTIVA (user_premium)
        // Entrenamientos en días consecutivos hasta HOY
        // =====================================================

        Routine streakRoutine = r1;

        for (int i = 4; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now()
                    .minusDays(i)
                    .withHour(18)
                    .withMinute(0);

                createWorkoutSession(
                    "carlos.lopez",
                    streakRoutine,
                    start,
                    50,
                    spec("Squat", 3, 8, bd(60 + i)),
                    spec("Bench Press", 3, 8, bd(45 + i)),
                    spec("Plank", 3, 30, null));

            List<WorkoutSession> user2Sessions = workoutSessionDao.findByUserOrderByFechaDesc(user2);

            for (WorkoutSession session : user2Sessions) {
                ensureLike("maria.garcia", session);
                ensureLike("andres.sanchez", session);
            }

            List<WorkoutSession> user1Sessions = workoutSessionDao.findByUserOrderByFechaDesc(user1);

            for (WorkoutSession session : user1Sessions) {
                ensureLike("carlos.lopez", session);
            }

            if (!user2Sessions.isEmpty()) {
                WorkoutSession popular = user2Sessions.get(0);

                ensureLike("maria.garcia", popular);
                ensureLike("laura.fernandez", popular);
                ensureLike("andres.sanchez", popular);
                ensureLike("elena.martin", popular);
            }

            if (!user2Sessions.isEmpty()) {
                WorkoutSession s = user2Sessions.get(0);

                createComment("maria.garcia", s, "Great session, keep it up!");
                createComment("andres.sanchez", s, "Nice intensity, good job!");
            }
            if (!user1Sessions.isEmpty()) {
                WorkoutSession s = user1Sessions.get(0);

                createComment("carlos.lopez", s, randomComment(USER_COMMENTS));
            }
            if (!user2Sessions.isEmpty()) {
                WorkoutSession popular = user2Sessions.get(0);

                createComment("laura.fernandez", popular, "Amazing work 💪");
                createComment("andres.sanchez", popular, "Very solid execution.");
                createComment("elena.martin", popular, "You are progressing fast!");
            }

        }

    }

    private void createWorkoutSession(
            String username,
            Routine routine,
            LocalDateTime start,
            int durationMinutes,
            ExSpec... specs) {
        User user = userDao.findByUsername(username).orElseThrow();

        WorkoutSession session = new WorkoutSession(user, routine, start.toLocalDate().atStartOfDay());
        session.setStartTime(start);
        session.setEndTime(start.plusMinutes(durationMinutes));
        workoutSessionDao.save(session);

        streakService.registrarEntrenamientoEnFecha(
                user.getId(),
                start.toLocalDate());

        for (ExSpec s : specs) {
            Exercise ex = exerciseDao.findAll().stream()
                    .filter(e -> s.name.equalsIgnoreCase(e.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Exercise not found: " + s.name));

            for (int set = 1; set <= s.sets; set++) {
                setLogDao.save(new SetLog(
                        session,
                        ex,
                        set,
                        s.reps,
                        s.weight));
            }
        }

    }

    private void ensureFollow(String followerUsername, String followedUsername) {
        User follower = userDao.findByUsername(followerUsername).orElseThrow();
        User followed = userDao.findByUsername(followedUsername).orElseThrow();

        if (!followDao.existsByFollowerIdAndCoachId(follower.getId(), followed.getId())) {
            followDao.save(new Follow(follower, followed));
        }
    }

    private String randomComment(String[] comments) {
        return comments[new java.util.Random().nextInt(comments.length)];
    }

    private void ensureLike(String likerUsername, WorkoutSession session) {
        User liker = userDao.findByUsername(likerUsername).orElseThrow();
        User liked = session.getUser(); // dueño de la sesión

        if (!likeDao.existsByLikerIdAndSessionId(liker.getId(), session.getId())) {
            likeDao.save(new Like(liker, liked, session));
        }
    }

    private void createComment(
            String commenterUsername,
            WorkoutSession session,
            String text) {

        User commenter = userDao.findByUsername(commenterUsername).orElseThrow();
        User commented = session.getUser(); // dueño de la sesión

        Comment comment = new Comment(session, commenter, commented, text);
        commentDao.save(comment);
    }

    private void ensureDemoUser(String username, String displayName, String firstName, String lastName,
            String rawPassword) {
        if (!userDao.findByUsername(username).isPresent()) {
            User nu = new User(displayName, passwordEncoder.encode(rawPassword), firstName, lastName, username);
            nu.setRole(User.RoleType.USER);
            userDao.save(nu);
        }
    }

    private void ensureDemoCoach(String username, String displayName, String firstName, String lastName,
            String rawPassword) {
        userDao.findByUsername(username).ifPresentOrElse(u -> {
            if (u.getRole() != User.RoleType.COACH) {
                u.setRole(User.RoleType.COACH);
                userDao.save(u);
            }
        }, () -> {
            User nu = new User(displayName, passwordEncoder.encode(rawPassword), firstName, lastName, username);
            nu.setRole(User.RoleType.COACH);
            userDao.save(nu);
        });
    }

    private Routine getOrCreateRoutineForCoach(String routineName, String coachUsername) {
        User coach = userDao.findByUsername(coachUsername).orElseThrow();

        List<Routine> coachRoutines = routineDao.findByUser(coach);
        for (Routine r : coachRoutines) {
            if (routineName.equalsIgnoreCase(r.getName())) {
                if (r.getEstado() != Routine.RoutineEstado.APPROVED) {
                    r.setEstado(Routine.RoutineEstado.APPROVED);
                    routineDao.save(r);
                }
                return r;
            }
        }

        Routine created = new Routine(routineName, coach);
        created.setEstado(Routine.RoutineEstado.APPROVED);
        routineDao.save(created);
        return created;
    }

    private void reassignRoutineToCoachIfNeeded(String routineName, String coachUsername) {
        Optional<User> maybeCoach = userDao.findByUsername(coachUsername);
        if (maybeCoach.isEmpty())
            return;
        User coach = maybeCoach.get();
        routineDao.findAll().stream()
                .filter(r -> routineName.equalsIgnoreCase(r.getName()))
                .filter(r -> r.getUser().getRole() != User.RoleType.COACH)
                .forEach(r -> {
                    r.setUser(coach);
                    r.setEstado(Routine.RoutineEstado.APPROVED);
                    routineDao.save(r);
                });
    }

    private static class ExSpec {
        final String name;
        final int sets;
        final int reps;
        final BigDecimal weight;

        ExSpec(String n, int s, int r, BigDecimal w) {
            this.name = n;
            this.sets = s;
            this.reps = r;
            this.weight = w;
        }
    }

    private static ExSpec spec(String name, int sets, int reps, BigDecimal weight) {
        return new ExSpec(name, sets, reps, weight);
    }

    private static BigDecimal bd(double v) {
        return BigDecimal.valueOf(v);
    }

    private void ensureRoutineExercises(Routine routine, ExSpec... specs) {
        if (routine == null || routineExerciseDao.countByRoutine(routine) > 0)
            return;

        for (ExSpec s : specs) {
            Exercise ex = exerciseDao.findAll().stream()
                    .filter(e -> s.name.equalsIgnoreCase(e.getName()))
                    .findFirst()
                    .orElseGet(() -> {
                        Exercise ne = new Exercise(s.name, s.name, "");
                        ne.setestado(Exercise.ExerciseEstado.APPROVED);
                        return exerciseDao.save(ne);
                    });
            RoutineExercise re = new RoutineExercise(routine, ex, s.sets, s.reps);
            re.setWeight(s.weight);
            routineExerciseDao.save(re);
        }
    }
}