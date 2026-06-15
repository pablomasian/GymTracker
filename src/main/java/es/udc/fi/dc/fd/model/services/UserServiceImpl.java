package es.udc.fi.dc.fd.model.services;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.services.exceptions.BlockedUserException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectPasswordException;
import es.udc.fi.dc.fd.model.entities.BlockDao;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BlockDao blockDao;

    // Registro de usuario
    @Override
    public void signUp(User user) throws DuplicateInstanceException {

        if (userDao.existsByUsername(user.getUsername())) {
            throw new DuplicateInstanceException("project.entities.user", user.getUsername());
        }

        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            user.setAvatarUrl("/uploads/" + user.getUsername() + "/avatar.png");
        } else {
            user.setAvatarUrl("/uploads/default/avatar.png");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(User.RoleType.USER);
        }

        userDao.save(user);
    }

    // Guardar avatar
    @Override
    public User saveUserAvatar(String username, MultipartFile file) throws IOException {
        final String UPLOAD_DIR = "uploads";

        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Path userDir = Paths.get(UPLOAD_DIR, username);
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".png";

        Path filePath = userDir.resolve("avatar" + extension);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setAvatarUrl("/uploads/" + username + "/avatar" + extension);
        userDao.save(user);

        System.out.println("Guardando archivo en: " + filePath.toAbsolutePath());

        return user;
    }

    // Login
    @Override
    @Transactional(readOnly = true)
    public User login(String username, String password) throws IncorrectLoginException, BlockedUserException {
        Optional<User> user = userDao.findByUsername(username);
        if (!user.isPresent() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new IncorrectLoginException(username, password);
        }

        if (Boolean.TRUE.equals(user.get().getBlocked())) {
            throw new BlockedUserException(username);
        }

        return user.get();
    }

    @Override
    @Transactional(readOnly = true)
    public User loginFromId(Long id) throws InstanceNotFoundException {
        return permissionChecker.checkUser(id);
    }

    // ÚNICO updateProfile
    @Override
    public User updateProfile(Long id,
                              String firstName,
                              String lastName,
                              String username,
                              String nombreUsuario,
                              String email,
                              Double weight,
                              Double height,
                              Integer age,
                              String gender,
                              Boolean premium) throws InstanceNotFoundException {

        User user = permissionChecker.checkUser(id);

        // Datos generales
        if (firstName != null) user.setFirstName(firstName.trim());
        if (lastName != null) user.setLastName(lastName.trim());
        if (username != null) user.setUsername(username.trim());
        if (nombreUsuario != null && !nombreUsuario.isBlank()) {
            user.setNombreUsuario(nombreUsuario.trim());
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email.trim());
        }

        // Datos fitness
        if (weight != null) user.setWeight(weight);
        if (height != null) user.setHeight(height);
        if (age != null) user.setAge(age);
        if (gender != null) user.setGender(gender);

        // Premium (solo coaches)
        if (user.getRole() == User.RoleType.COACH && premium != null) {
            user.setPremium(premium);
        }

        userDao.save(user);
        return user;
    }

    // Cambiar contraseña
    @Override
    public void changePassword(Long id, String oldPassword, String newPassword)
            throws InstanceNotFoundException, IncorrectPasswordException {

        User user = permissionChecker.checkUser(id);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException();
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
    }

    // Perfil público coach
    @Override
    public User getCoachProfile(Long coach_id) {
        return userDao.findById(coach_id).orElseThrow(null);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<User> listAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void blockUser(Long userId) throws InstanceNotFoundException {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new InstanceNotFoundException("entities.user", userId));
        user.setBlocked(true);
        userDao.save(user);
    }

    @Override
    public void unblockUser(Long userId) throws InstanceNotFoundException {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new InstanceNotFoundException("entities.user", userId));
        user.setBlocked(false);
        userDao.save(user);
    }

    // Estado premium
    @Override
    public User updatePremiumStatus(Long userId, Boolean premium) throws InstanceNotFoundException {
        User user = permissionChecker.checkUser(userId);
        user.setPremium(premium);
        userDao.save(user);
        return user;
    }

	@Override
	@Transactional(readOnly = true)
	public List<User> searchUsers(String query, Long requesterId) {

		String normalized = query.toLowerCase().trim();

		List<User> matches = userDao.findByUsernameContainingIgnoreCase(normalized);

		List<Long> blockedByMe = blockDao.findBlockedUserIdsByBlockerId(requesterId);
		List<Long> whoBlockedMe = blockDao.findBlockerUserIdsByBlockedId(requesterId);

		return matches.stream()
				.filter(u -> !u.getId().equals(requesterId))
				.filter(u -> !Boolean.TRUE.equals(u.getBlocked()))
				.filter(u -> !blockedByMe.contains(u.getId()))
				.filter(u -> !whoBlockedMe.contains(u.getId()))
				.toList();
	}


    @Override
    @Transactional(readOnly = true)
    public User getPublicUserProfile(Long id) throws InstanceNotFoundException {
        return userDao.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("entities.user", id));
    }

}
