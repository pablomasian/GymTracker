package es.udc.fi.dc.fd.rest.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.UserService;

@WebMvcTest(UserController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class UserControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private es.udc.fi.dc.fd.rest.common.JwtGenerator jwtGenerator;

    @MockBean
    private org.springframework.context.MessageSource messageSource;

    @MockBean
    private es.udc.fi.dc.fd.model.services.BadgeService badgeService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.FollowService followService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.WorkoutService workoutService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.BlockService blockService;

    @Test
    void getPublicProfile_notFound_returns404() throws Exception {
        when(userService.getPublicUserProfile(10L)).thenThrow(new es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException("project.entities.user", 10L));

        mockMvc.perform(get("/api/users/public-profile/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPublicProfile_found_returnsDto() throws Exception {
        User user = new User();
        user.setId(20L);
        user.setNombreUsuario("u20");
        user.setUsername("user20");
        user.setRole(es.udc.fi.dc.fd.model.entities.User.RoleType.USER);

        when(userService.getPublicUserProfile(20L)).thenReturn(user);

        mockMvc.perform(get("/api/users/public-profile/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.nombreUsuario").value("u20"));
    }
}
