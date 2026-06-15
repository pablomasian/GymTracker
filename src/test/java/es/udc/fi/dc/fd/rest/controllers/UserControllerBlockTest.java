package es.udc.fi.dc.fd.rest.controllers;

import es.udc.fi.dc.fd.model.services.BlockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class UserControllerBlockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private es.udc.fi.dc.fd.model.services.UserService userService;
    @MockBean
    private es.udc.fi.dc.fd.model.services.FollowService followService;
    @MockBean
    private es.udc.fi.dc.fd.model.services.WorkoutService workoutService;
    @MockBean
    private BlockService blockService;
    @MockBean
    private es.udc.fi.dc.fd.model.services.BadgeService badgeService;
    @MockBean
    private org.springframework.context.MessageSource messageSource;
    @MockBean
    private es.udc.fi.dc.fd.rest.common.JwtGenerator jwtGenerator;

    @Test
    void testBlockUserEndpoint() throws Exception {
        doNothing().when(blockService).blockUser(1L, 2L);

        mockMvc.perform(post("/api/users/2/block")
                .requestAttr("userId", 1L))
                .andExpect(status().isNoContent());

        verify(blockService).blockUser(1L, 2L);
    }

    @Test
    void testUnblockUserEndpoint() throws Exception {
        doNothing().when(blockService).unblockUser(1L, 2L);

        mockMvc.perform(delete("/api/users/2/block")
                .requestAttr("userId", 1L))
                .andExpect(status().isNoContent());

        verify(blockService).unblockUser(1L, 2L);
    }

    @Test
    void testIsUserBlocked() throws Exception {
        when(blockService.isBlocked(1L, 2L)).thenReturn(true);

        mockMvc.perform(get("/api/users/2/blocked")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(blockService).isBlocked(1L, 2L);
    }
}
