package es.udc.fi.dc.fd.rest.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.RoutineService;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;

@WebMvcTest(RoutinesController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class RoutinesControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoutineService routineService;

    @MockBean
    private RoutineExerciseDao routineExerciseDao;
    
    @MockBean
    private es.udc.fi.dc.fd.rest.common.JwtGenerator jwtGenerator;

    @MockBean
    private org.springframework.context.MessageSource messageSource;

    @MockBean
    private es.udc.fi.dc.fd.model.services.UserService userService;

    @MockBean
    private es.udc.fi.dc.fd.model.entities.RoutineDao routineDao;

    @MockBean
    private es.udc.fi.dc.fd.model.entities.ExerciseDao exerciseDao;

    @MockBean
    private es.udc.fi.dc.fd.model.services.RoutineExerciseService routineExerciseService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.NotificationsFollowedCoachService notificationsFollowedCoachService;

    @Test
    void displayAllRoutines_returnsList() throws Exception {
        User u = new User();
        u.setId(10L);
        u.setNombreUsuario("coach1");

        Routine r = new Routine("Rutina A", u);
        r.setId(1L);

        when(routineService.findPublic()).thenReturn(List.of(r));
        when(routineExerciseDao.countByRoutine(r)).thenReturn(2);

        mockMvc.perform(get("/api/routines/display_all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Rutina A"))
                .andExpect(jsonPath("$[0].exerciseCount").value(2));
    }

    @Test
    void getRoutine_notVisible_withoutUser_returns404() throws Exception {
        User u = new User();
        u.setId(2L);
        u.setNombreUsuario("coach2");

        Routine r = new Routine("Privada", u);
        r.setId(5L);
        r.setVisible(false);

        when(routineService.findById(5L)).thenReturn(r);

        mockMvc.perform(get("/api/routines/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRoutine_visible_returnsDto() throws Exception {
        User u = new User();
        u.setId(7L);
        u.setNombreUsuario("coach7");

        Routine r = new Routine("Publica", u);
        r.setId(8L);
        r.setVisible(true);

        when(routineService.findById(8L)).thenReturn(r);
        when(routineExerciseDao.countByRoutine(r)).thenReturn(3);

        mockMvc.perform(get("/api/routines/8").requestAttr("userId", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.name").value("Publica"))
                .andExpect(jsonPath("$.exerciseCount").value(3));
    }
}
