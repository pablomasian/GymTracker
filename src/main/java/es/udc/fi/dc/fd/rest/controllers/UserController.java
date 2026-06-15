package es.udc.fi.dc.fd.rest.controllers;

import static es.udc.fi.dc.fd.rest.dtos.UserConversor.toAuthenticatedUserDto;
import static es.udc.fi.dc.fd.rest.dtos.UserConversor.toUser;
import static es.udc.fi.dc.fd.rest.dtos.UserConversor.toUserDto;

import es.udc.fi.dc.fd.model.entities.WorkoutSession;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import es.udc.fi.dc.fd.model.services.WorkoutService;
import es.udc.fi.dc.fd.rest.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.exceptions.BlockedUserException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectPasswordException;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.FollowService;
import es.udc.fi.dc.fd.model.services.UserService;
import es.udc.fi.dc.fd.model.services.BlockService;
import es.udc.fi.dc.fd.model.services.BadgeService;
import es.udc.fi.dc.fd.model.services.exceptions.AlreadyBlockedException;
import es.udc.fi.dc.fd.model.services.exceptions.NotBlockedException;
import es.udc.fi.dc.fd.rest.common.ErrorsDto;
import es.udc.fi.dc.fd.rest.common.JwtGenerator;
import es.udc.fi.dc.fd.rest.common.JwtInfo;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";
    private static final String INCORRECT_PASS_EXCEPTION_CODE = "project.exceptions.IncorrectPasswordException";
    private static final String BLOCKED_USER_EXCEPTION_CODE = "project.exceptions.BlockedUserException";
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;
    @Autowired
    private WorkoutService workoutService;
    @Autowired
    private BlockService blockService;

    /* ===================== EXCEPCIONES ===================== */

    @ExceptionHandler(IncorrectLoginException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorsDto handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {
        String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
            "Incorrect username or password.", locale);
        return new ErrorsDto(errorMessage);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorsDto handleIncorrectPasswordException(IncorrectPasswordException exception, Locale locale) {
        String errorMessage = messageSource.getMessage(INCORRECT_PASS_EXCEPTION_CODE, null,
            "Incorrect password.", locale);
        return new ErrorsDto(errorMessage);
    }

    @ExceptionHandler(BlockedUserException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorsDto handleBlockedUserException(BlockedUserException exception, Locale locale) {
        String errorMessage = messageSource.getMessage(BLOCKED_USER_EXCEPTION_CODE, null,
                "Your account has been blocked by an administrator", locale);
        return new ErrorsDto(errorMessage);
    }

    /* ===================== SIGN UP ===================== */

    @PostMapping("/signUp")
    public ResponseEntity<AuthenticatedUserDto> signUp(
            @Validated({ UserDto.AllValidations.class }) @RequestBody UserDto userDto)
            throws DuplicateInstanceException {

        User user = toUser(userDto);
        userService.signUp(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location)
                .body(toAuthenticatedUserDto(generateServiceToken(user), user));
    }

    /* ===================== AVATAR ===================== */

    @PostMapping("/{username}/avatar")
    public ResponseEntity<User> uploadAvatar(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file) {

        try {
            User updatedUser = userService.saveUserAvatar(username, file);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error uploading avatar for user: " + username, e);

            return ResponseEntity.badRequest().build();
        }
    }

    /* ===================== LOGIN ===================== */

    @PostMapping("/login")
    public AuthenticatedUserDto login(@Validated @RequestBody LoginParamsDto params)
            throws IncorrectLoginException, BlockedUserException {

        User user = userService.login(params.getUsername(), params.getPassword());
        return toAuthenticatedUserDto(generateServiceToken(user), user);
    }

    @PostMapping("/loginFromServiceToken")
    public AuthenticatedUserDto loginFromServiceToken(@RequestAttribute Long userId,
            @RequestAttribute String serviceToken)
            throws InstanceNotFoundException {
        User user = userService.loginFromId(userId);
        return toAuthenticatedUserDto(serviceToken, user);
    }

    /* ===================== UPDATE PROFILE ===================== */

    // Datos privados (fitness)
    @PutMapping("/{id}")
    public UserPrivateDto updateFitnessProfile(@RequestAttribute Long userId,
            @PathVariable Long id,
            @RequestBody UserPrivateDto dto)
            throws InstanceNotFoundException, PermissionException {

        if (!id.equals(userId))
            throw new PermissionException();

        User updated = userService.updateProfile(id,
                null, null, null, null, null, // públicos
                dto.getWeight(), dto.getHeight(), dto.getAge(), dto.getGender(), // privados
                null); // premium no cambia aquí

        return UserConversor.toUserPrivateDto(updated);
    }

    // Datos públicos
    @PutMapping("/{id}/public")
    public UserDto updatePublicProfile(@RequestAttribute Long userId,
            @PathVariable Long id,
            @RequestBody UserDto dto)
            throws InstanceNotFoundException, PermissionException {

        if (!id.equals(userId))
            throw new PermissionException();

        User updated = userService.updateProfile(id,
                dto.getFirstName(), dto.getLastName(), dto.getUsername(),
                dto.getNombreUsuario(), dto.getEmail(),
                null, null, null, null, // privados
                dto.getPremium()); // premium solo afecta si es COACH

        return UserConversor.toUserDto(updated);
    }

    @GetMapping("/{id}/private")
    public UserPrivateDto getPrivateProfile(@RequestAttribute Long userId, @PathVariable Long id)
            throws InstanceNotFoundException, PermissionException {

        if (!id.equals(userId))
            throw new PermissionException();

        User user = userService.loginFromId(id);
        return UserConversor.toUserPrivateDto(user);
    }

    /* ===================== PASSWORD ===================== */

    @PostMapping("/{id}/changePassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestAttribute Long userId, @PathVariable Long id,
            @Validated @RequestBody ChangePasswordParamsDto params)
            throws PermissionException, InstanceNotFoundException, IncorrectPasswordException {

        if (!id.equals(userId))
            throw new PermissionException();
        userService.changePassword(id, params.getOldPassword(), params.getNewPassword());
    }

    /* ===================== PUBLIC USER PROFILE ===================== */

    // Perfil público de usuario
    @GetMapping("/public-profile/{id}")
    public ResponseEntity<UserDto> getPublicProfile(@RequestAttribute(required = false) Long userId, 
                                                      @PathVariable Long id) {
        try {
            User user = userService.getPublicUserProfile(id);
            return ResponseEntity.ok(UserConversor.toUserDto(user));
        } catch (InstanceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public-profile/{id}/performed-routines")
    public ResponseEntity<List<WorkoutSessionDto>> getPublicPerformedRoutines(
            @RequestAttribute(required = false) Long userId,
            @PathVariable Long id) {
        try {
            // Si hay bloqueo, devolver lista vacía
            if (userId != null && (blockService.isBlocked(userId, id) || blockService.hasBlockedMe(userId, id))) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            List<WorkoutSession> sessions = workoutService.getPublicWorkoutSessionsByUser(id);
            List<WorkoutSessionDto> dtos = sessions.stream().map(s -> {
                WorkoutSessionDto dto = new WorkoutSessionDto();
                dto.setId(s.getId());
                dto.setUserId(s.getUser().getId());
                dto.setUserName(s.getUser().getNombreUsuario());
                dto.setRoutineId(s.getRoutine().getId());
                dto.setRoutineName(s.getRoutine().getName());
                dto.setFecha(s.getFecha());
                dto.setStartTime(s.getStartTime());
                dto.setEndTime(s.getEndTime());
                dto.setLiked(false);
                return dto;
            }).toList();

            return ResponseEntity.ok(dtos);
        } catch (InstanceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /* ===================== COACH PROFILE ===================== */

    @GetMapping("/coach-profile/{coach_id}")
    public UserDto getCoachProfile(@PathVariable Long coach_id) {
        return UserConversor.toUserDto(userService.getCoachProfile(coach_id));
    }

    @GetMapping("/coach-profile/{coach_id}/following")
    public boolean alreadyFollowing(@PathVariable Long coach_id, @RequestParam Long user_id) {
        return followService.alreadyFollowing(user_id, coach_id);
    }

    @PutMapping("/coach-profile/{coach_id}")
    public void startFollowing(@PathVariable Long coach_id, @RequestParam Long user_id) {
        followService.start_following(user_id, coach_id);
    }

    @DeleteMapping("/coach-profile/{coach_id}")
    public void stopFollowing(@PathVariable Long coach_id, @RequestParam Long user_id) {
        followService.stop_following(user_id, coach_id);
    }

    /* ===================== FOLLOW ===================== */

    /*
     * ===================== UNIVERSAL FOLLOW (users & coaches)
     * =====================
     */

    @GetMapping("/{targetId}/following")
    public boolean isFollowing(@PathVariable Long targetId, @RequestParam Long user_id) {
        return followService.alreadyFollowing(user_id, targetId);
    }

    @PutMapping("/{targetId}/follow")
    public void follow(@PathVariable Long targetId, @RequestParam Long user_id) {
        followService.start_following(user_id, targetId);
    }

    @DeleteMapping("/{targetId}/follow")
    public void unfollow(@PathVariable Long targetId, @RequestParam Long user_id) {
        followService.stop_following(user_id, targetId);
    }

    // Lista todos los coaches que sigue un usuario
    @GetMapping("/{userId}/following-list")
    public List<FollowedCoachDto> getFollowingList(@PathVariable Long userId) {
        List<User> followedCoaches = followService.getFollowingList(userId);

        return followedCoaches.stream()
                .map(c -> new FollowedCoachDto(
                        c.getId(),
                        c.getUsername(),
                        c.getNombreUsuario(),
                        c.getAvatarUrl(),
                        c.getRole().toString()))
                .collect(Collectors.toList());
    }

    // Lista todos los seguidores de un coach
    @GetMapping("/{userId}/followers-list")
    public List<UserDto> getFollowersList(@PathVariable Long userId) {
        List<User> followers = followService.getFollowersList(userId);

        return followers.stream()
                .map(UserConversor::toUserDto)
                .collect(Collectors.toList());
    }

    /* ===================== PREMIUM ===================== */

    @PutMapping("/{targetUserId}/toggle-premium")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto togglePremium(@RequestAttribute Long userId,
            @PathVariable Long targetUserId)
            throws InstanceNotFoundException, PermissionException {

        User admin = userService.loginFromId(userId);
        if (admin.getRole() != User.RoleType.ADMIN)
            throw new PermissionException();

        User targetUser = userService.loginFromId(targetUserId);
        boolean newPremiumStatus = targetUser.getPremium() == null || !targetUser.getPremium();

        User updated = userService.updatePremiumStatus(targetUserId, newPremiumStatus);
        return toUserDto(updated);
    }

    /* ===================== ADMIN ===================== */

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public java.util.List<UserDto> listAllUsers() {
        return userService.listAllUsers().stream()
                .map(UserConversor::toUserDto)
                .toList();
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockUser(@PathVariable("id") Long userId) throws InstanceNotFoundException {
        userService.blockUser(userId);
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblockUser(@PathVariable("id") Long userId) throws InstanceNotFoundException {
        userService.unblockUser(userId);
    }

    /* ===================== TOKEN ===================== */

    private String generateServiceToken(User user) {
        JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getNombreUsuario(), user.getRole().toString());
        return jwtGenerator.generate(jwtInfo);
    }

    @GetMapping("/search")
    public List<UserDto> searchUsers(
            @RequestParam String query,
            @RequestAttribute(name = "userId", required = false) Long userId) {

        return userService.searchUsers(query, userId)
                .stream()
                .map(UserConversor::toUserDto)
                .toList();
    }

	/* ===================== BLOCK USER ===================== */

	@PostMapping("/{targetUserId}/block")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void blockUserEndpoint(@RequestAttribute Long userId,
								   @PathVariable Long targetUserId)
			throws InstanceNotFoundException, AlreadyBlockedException {
		blockService.blockUser(userId, targetUserId);
	}

	@DeleteMapping("/{targetUserId}/block")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unblockUserEndpoint(@RequestAttribute Long userId,
									 @PathVariable Long targetUserId)
			throws InstanceNotFoundException, NotBlockedException {
		blockService.unblockUser(userId, targetUserId);
	}

	@GetMapping("/{targetUserId}/blocked")
	public boolean isUserBlocked(@RequestAttribute Long userId,
								  @PathVariable Long targetUserId) {
		return blockService.isBlocked(userId, targetUserId);
	}

    /*===================== BADGES =========================*/
    @GetMapping("/badges")
    public List<BadgeDto> getBadges (@RequestAttribute Long userId){
        return BadgeConversor.toBadgeDtos(badgeService.getBadgesByUser(userId));
    }
    

}
