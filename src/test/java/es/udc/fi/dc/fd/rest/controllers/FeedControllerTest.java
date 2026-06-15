package es.udc.fi.dc.fd.rest.controllers;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;

import es.udc.fi.dc.fd.model.entities.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.fi.dc.fd.model.services.SocialService;
import es.udc.fi.dc.fd.model.services.FollowService;
import es.udc.fi.dc.fd.model.services.WorkoutService;
import es.udc.fi.dc.fd.model.entities.UserDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocialService socialService;

    @MockBean
    private FollowService followService;

    @MockBean
    private WorkoutService workoutService;

    @MockBean
    private UserDao userDao;

    @Test
    public void testGetFeed() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);

        when(userDao.getById(1L)).thenReturn(mockUser);

        // ❗ Solución aplicada: listas modificables
        when(followService.allFollowed(1L)).thenReturn(new ArrayList<>());
        when(workoutService.getWorkoutSessionsByUser(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/feed/workouts")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testInteractions() throws Exception {
        mockMvc.perform(post("/api/feed/like/1")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/feed/unlike/1")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/feed/1/comment")
                        .param("text", "Good job")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk());
    }
}
