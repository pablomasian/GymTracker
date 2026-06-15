package es.udc.fi.dc.fd.rest.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.fi.dc.fd.model.services.WorkoutService;
import es.udc.fi.dc.fd.model.services.FollowService;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.rest.dtos.WorkoutSessionConversor;
import java.util.Collections;
import java.util.ArrayList;
import es.udc.fi.dc.fd.model.entities.User;

@WebMvcTest(FeedController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeedControllerWebTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WorkoutService workoutService;

    @MockBean
    private FollowService followService;

    @MockBean
    private UserDao userDao;

    @MockBean
    private WorkoutSessionConversor sessionConversor;

    @MockBean
    private es.udc.fi.dc.fd.rest.common.JwtGenerator jwtGenerator;

    @MockBean
    private es.udc.fi.dc.fd.model.services.UserService userService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.SocialService socialService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.SetLogService setLogService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.BlockService blockService;

    @Test
    void getFeed_whenNoData_returnsEmptyList() throws Exception {
    when(userDao.getById(anyLong())).thenReturn(new User());
    when(followService.allFollowed(anyLong())).thenReturn(new ArrayList<>());
    when(workoutService.getWorkoutSessionsByUser(anyLong())).thenReturn(new ArrayList<>());
    when(sessionConversor.toWorkoutSessionDtosFeed(any(), anyLong())).thenReturn(new ArrayList<>());

    mvc.perform(get("/api/feed/workouts").requestAttr("userId", 1L)).andExpect(status().isOk());

    verify(userDao, times(1)).getById(anyLong());
    verify(followService, times(1)).allFollowed(anyLong());
    }

}
