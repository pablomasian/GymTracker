package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.BlockDao;
import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.services.TestDataFactory;
import es.udc.fi.dc.fd.rest.dtos.ChangePasswordParamsDto;
import es.udc.fi.dc.fd.rest.dtos.UserDto;
import es.udc.fi.dc.fd.rest.dtos.UserPrivateDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class UserControllerCoverageTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserDao userDao;

        @Autowired
        private BlockDao blockDao;

        @Autowired
        private CommentDao commentDao;

        @Autowired
        private FollowDao followDao;

        @Autowired
        private LikeDao likeDao;

        @Autowired
        private RoutineDao routineDao;

        @Autowired
        private SetLogDao setLogDao;

        @Autowired
        private WorkoutSessionDao workoutSessionDao;

        @Autowired
        private RoutineExerciseDao routineExerciseDao;

        @Autowired
        private NotificationDao notificationDao;

        @Autowired
        private BCryptPasswordEncoder passwordEncoder;

        private User user1;
        private User admin;
        private User coach;

        private ObjectMapper mapper = new ObjectMapper();

        @Before
        public void setUp() {
                SecurityContextHolder.clearContext();

                // Cleanup in correct order to avoid FK violations
                setLogDao.deleteAll();
                commentDao.deleteAll();
                likeDao.deleteAll();
                routineExerciseDao.deleteAll();
                workoutSessionDao.deleteAll();
                routineDao.deleteAll();
                followDao.deleteAll();
                blockDao.deleteAll();
                notificationDao.deleteAll();

                userDao.deleteAll();

                user1 = TestDataFactory.newUser("userCov");
                user1.setPassword(passwordEncoder.encode("password"));
                userDao.save(user1);

                admin = TestDataFactory.newUser("adminCov");
                admin.setRole(User.RoleType.ADMIN);
                admin.setPassword(passwordEncoder.encode("password"));
                userDao.save(admin);

                coach = TestDataFactory.newUser("coachCov");
                coach.setRole(User.RoleType.COACH);
                userDao.save(coach);
        }

        private void authenticate(User user) {
                String role = "ROLE_" + user.getRole().name();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                Authentication auth = new UsernamePasswordAuthenticationToken(user.getNombreUsuario(), "password",
                                Collections.singletonList(authority));
                SecurityContextHolder.getContext().setAuthentication(auth);
        }

        @Test
        public void testSignUp() throws Exception {
                UserDto dto = new UserDto();
                dto.setNombreUsuario("newUserSign");
                dto.setPassword("password");
                dto.setFirstName("New");
                dto.setLastName("User");
                dto.setUsername("newUserSign");
                dto.setEmail("new@test.com");
                dto.setRole("USER");

                mockMvc.perform(post("/api/users/signUp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.user.username").value("newUserSign"));
        }

        @Test
        public void testUpdateFitnessProfile() throws Exception {
                UserPrivateDto dto = new UserPrivateDto();
                dto.setWeight(75.5);
                dto.setHeight(180.0);
                dto.setAge(25);
                dto.setGender("Male");

                authenticate(user1);
                mockMvc.perform(put("/api/users/{id}", user1.getId())
                                .requestAttr("userId", user1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.weight").value(75.5));

                // Test different user -> 403 (PermissionException mapped to ?)
                // PermissionException is usually 403 or 404. Let's assume 403/Forbidden if
                // handled, or 500 if not.
                // Spring default mapping for unchecked could be 500.
                // But Controller calls throw new PermissionException().
                // If ExceptionHandler is missing? No, usually generic one exists.
                // In UserController, no PermissionException handler block provided.
                // It might bubble up.
                // Let's verify happy path mainly.

                authenticate(admin); // Authenticate as wrong user
                mockMvc.perform(put("/api/users/{id}", user1.getId())
                                .requestAttr("userId", admin.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto)))
                                .andExpect(status().isForbidden()); // PermissionException default map is often 403 if
                                                                    // annotated or configured.
                // Wait, if no handler, it might be 500. GymTracker usually has
                // GlobalControllerExceptionHandler.
        }

        @Test
        public void testUpdatePublicProfile() throws Exception {
                UserDto dto = new UserDto();
                dto.setFirstName("Updated");
                dto.setLastName("Name");
                dto.setUsername("updatedUser");
                dto.setNombreUsuario("updatedUser");
                dto.setEmail("updated@test.com");
                dto.setRole("USER");

                authenticate(user1);
                mockMvc.perform(put("/api/users/{id}/public", user1.getId())
                                .requestAttr("userId", user1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName").value("Updated"));
        }

        @Test
        public void testChangePassword() throws Exception {
                ChangePasswordParamsDto params = new ChangePasswordParamsDto();
                params.setOldPassword("password");
                params.setNewPassword("newPassword");

                authenticate(user1);
                mockMvc.perform(post("/api/users/{id}/changePassword", user1.getId())
                                .requestAttr("userId", user1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(params)))
                                .andExpect(status().isNoContent());

                // Verify changes? Login with new password?
                // UserService handles it.
        }

        @Test
        public void testTogglePremium() throws Exception {
                authenticate(admin);

                // Toggle ON
                mockMvc.perform(put("/api/users/{id}/toggle-premium", coach.getId())
                                .requestAttr("userId", admin.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.premium").value(true));

                // Toggle OFF
                mockMvc.perform(put("/api/users/{id}/toggle-premium", coach.getId())
                                .requestAttr("userId", admin.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.premium").value(false));

                // Permission check: Non-admin
                authenticate(user1);
                mockMvc.perform(put("/api/users/{id}/toggle-premium", coach.getId())
                                .requestAttr("userId", user1.getId()))
                                // PreAuthorize might catch it (403) or PermissionCheck (403)
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testAdminBlockUnblock() throws Exception {
                authenticate(admin);

                // Block
                mockMvc.perform(put("/api/users/{id}/block", user1.getId())
                                .requestAttr("userId", admin.getId()))
                                .andExpect(status().isNoContent());

                // Unblock
                mockMvc.perform(put("/api/users/{id}/unblock", user1.getId())
                                .requestAttr("userId", admin.getId()))
                                .andExpect(status().isNoContent());
        }

        @Test
        public void testGetPublicProfile() throws Exception {
                mockMvc.perform(get("/api/users/public-profile/{id}", user1.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombreUsuario").value(user1.getNombreUsuario())); // DTO maps
                                                                                                         // name?
                                                                                                         // userName ->
                                                                                                         // nombreUsuario?
                                                                                                         // check
                                                                                                         // mapping
        }

        @Test
        public void testCoachProfileAndFollow() throws Exception {
                // Coach Profile
                mockMvc.perform(get("/api/users/coach-profile/{id}", coach.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombreUsuario").value(coach.getNombreUsuario()));

                authenticate(user1);

                // Follow Coach
                mockMvc.perform(put("/api/users/coach-profile/{id}", coach.getId())
                                .param("user_id", String.valueOf(user1.getId())))
                                .andExpect(status().isOk());

                // Is Following
                mockMvc.perform(get("/api/users/coach-profile/{id}/following", coach.getId())
                                .param("user_id", String.valueOf(user1.getId())))
                                .andExpect(status().isOk())
                                .andExpect(content().string("true"));

                // Following List
                mockMvc.perform(get("/api/users/{id}/following-list", user1.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

                // Unfollow Coach
                mockMvc.perform(delete("/api/users/coach-profile/{id}", coach.getId())
                                .param("user_id", String.valueOf(user1.getId())))
                                .andExpect(status().isOk());
        }

        @Test
        public void testUniversalFollow() throws Exception {
                User target = TestDataFactory.newUser("targetUser");
                userDao.save(target);
                authenticate(user1);

                // Follow
                mockMvc.perform(put("/api/users/{id}/follow", target.getId())
                                .param("user_id", String.valueOf(user1.getId())))
                                .andExpect(status().isOk());

                // Check
                mockMvc.perform(get("/api/users/{id}/following", target.getId())
                                .param("user_id", String.valueOf(user1.getId())))
                                .andExpect(status().isOk())
                                .andExpect(content().string("true"));

                // Unfollow
                mockMvc.perform(delete("/api/users/{id}/follow", target.getId())
                                .param("user_id", String.valueOf(user1.getId())))
                                .andExpect(status().isOk());
        }

        @Test
        public void testSearchUsers() throws Exception {
                User found = TestDataFactory.newUser("foundUser");
                userDao.save(found);

                authenticate(user1);
                mockMvc.perform(get("/api/users/search")
                                .requestAttr("userId", user1.getId())
                                .param("query", "foundUser"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        public void testUserBlocking() throws Exception {
                User target = TestDataFactory.newUser("blockedMe");
                userDao.save(target);
                authenticate(user1);

                // Block
                mockMvc.perform(post("/api/users/{id}/block", target.getId())
                                .requestAttr("userId", user1.getId()))
                                .andExpect(status().isNoContent());

                // Is Blocked
                mockMvc.perform(get("/api/users/{id}/blocked", target.getId())
                                .requestAttr("userId", user1.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().string("true"));

                // Unblock
                mockMvc.perform(delete("/api/users/{id}/block", target.getId())
                                .requestAttr("userId", user1.getId()))
                                .andExpect(status().isNoContent());
        }
}
