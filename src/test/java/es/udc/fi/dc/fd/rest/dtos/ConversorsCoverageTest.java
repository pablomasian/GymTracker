package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.SetLog;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class ConversorsCoverageTest {

    @Test
    public void userConversorRoundtrip() {
        User u = new User("nu", "pw", "First", "Last", "uname");
        u.setId(123L);
        u.setEmail("  me@example.com  ");
        u.setAvatarUrl("/img/a.png");
        u.setPremium(Boolean.TRUE);
        u.setBlocked(Boolean.TRUE);
        u.setWeight(72.5);
        u.setHeight(180.0);
        u.setAge(30);
        u.setGender("M");

        UserDto dto = UserConversor.toUserDto(u);

        assertEquals(u.getId(), dto.getId());
        assertEquals(u.getNombreUsuario(), dto.getNombreUsuario());
        assertEquals(u.getFirstName(), dto.getFirstName());
        assertEquals(u.getLastName(), dto.getLastName());
        assertEquals(u.getUsername(), dto.getUsername());
        assertEquals(u.getEmail().trim(), dto.getEmail());
        assertEquals(u.getAvatarUrl(), dto.getAvatarUrl());
        assertEquals(u.getPremium(), dto.getPremium());
        assertEquals(u.getBlocked(), dto.getBlocked());

        // Roundtrip: toUser should ignore invalid roles and keep basic fields
        dto.setRole("INVALID_ROLE");
        dto.setPassword("secret");
        User u2 = UserConversor.toUser(dto);
        assertEquals(dto.getNombreUsuario(), u2.getNombreUsuario());
        assertEquals(dto.getPassword(), u2.getPassword());
        assertEquals(dto.getEmail(), u2.getEmail());

        // Private and public DTOs
        UserPrivateDto priv = UserConversor.toUserPrivateDto(u);
        assertEquals(u.getWeight(), priv.getWeight());
        UserDto pub = UserConversor.toUserPublicDto(u);
        assertEquals(u.getNombreUsuario(), pub.getNombreUsuario());
    }

    @Test
    public void exerciseConversor() {
        Exercise e = new Exercise("PushUp", "desc", "chest");
        e.setEquipment("none");
        e.setImageUrl("/i.png");
        e.setBlocked(true);
        // id is primitive long in entity; set via reflection if needed, but not required for conversor

        ExerciseDto dto = ExerciseConversor.toExerciseDto(e);
        assertEquals(e.getName(), dto.getName());
        assertEquals(e.getDescription(), dto.getDescription());
        assertEquals(e.getMuscles(), dto.getMuscles());
        assertEquals(e.getEquipment(), dto.getEquipment());
        assertEquals(e.getImageUrl(), dto.getImageUrl());
        assertTrue(dto.isBlocked());

        Exercise e2 = ExerciseConversor.toExercise(dto);
        assertEquals(dto.getName(), e2.getName());
        assertEquals(dto.getDescription(), e2.getDescription());
        assertEquals(dto.getMuscles(), e2.getMuscles());
    }

    @Test
    public void routineAndSetLogConversors() {
        User u = new User("nu", "pw", "F", "L", "uname");
        u.setId(5L);
        Routine r = new Routine("Legs", u);
        r.setId(77L);

        RoutineDto rdto = RoutineConversor.toRoutineDto(r);
        assertEquals(r.getId(), rdto.getId());
        assertEquals(r.getName(), rdto.getName());

        Routine r2 = RoutineConversor.toRoutine(rdto, u);
        assertEquals(rdto.getName(), r2.getName());

        WorkoutSession ws = new WorkoutSession(u, r, LocalDateTime.now());
        ws.setId(900L);

        Exercise e = new Exercise("Squat", "d", "legs");
        e.setImageUrl("/img.png");
        e.setBlocked(false);

        SetLog sl = new SetLog(ws, e, 1, 8, new BigDecimal("80"));
        sl.setId(300L);

        SetLogDto sld = SetLogConversor.toSetLogDto(sl);
        assertEquals(Long.valueOf(ws.getId()), sld.getSessionId());
        // exercise id is primitive long default 0; conversor expects exercise.getId() -> 0
        assertEquals(Long.valueOf(e.getId()), sld.getExerciseId());
        assertEquals(e.getName(), sld.getExerciseName());
    assertEquals(e.getImageUrl(), sld.getImageUrl());

        // list conversion
        assertEquals(1, SetLogConversor.toSetLogDtos(Arrays.asList(sl)).size());

        // WorkoutSession conversor static methods
        WorkoutSessionDto wsd = WorkoutSessionConversor.toWorkoutSessionDto(ws);
        assertEquals(ws.getId(), wsd.getId());
        assertEquals(ws.getUser().getId(), wsd.getUserId());
        assertEquals(ws.getRoutine().getId(), wsd.getRoutineId());

        WorkoutSession recreated = WorkoutSessionConversor.toWorkoutSession(wsd, u, r);
        assertEquals(wsd.getFecha(), recreated.getFecha());
    }

}
