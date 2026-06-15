package es.udc.fi.dc.fd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;

/**
 * Asegura que existan los usuarios de prueba "coach" (COACH) y "user" (USER)
 * en cada arranque de la aplicación. Si ya existen, los deja sin cambios.
 */
@Component
@Profile("!test")
public class SeedUsersRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedUsersRunner.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Use real-looking demo accounts instead of generic 'coach'/'user'
        ensureUser("ana.rodriguez", "Ana Rodríguez", User.RoleType.COACH);
        ensureUser("pedro.sanchez", "Pedro Sánchez", User.RoleType.USER);
    }

    private void ensureUser(String username, String displayName, User.RoleType role) {
        userDao.findByUsername(username).ifPresentOrElse(u -> {
            // Ya existe; si el rol cambió en el código podríamos actualizarlo (opcional):
            if (u.getRole() != role) {
                u.setRole(role);
                userDao.save(u);
                log.info("Rol actualizado para el usuario de prueba '{}' a {}", username, role);
            }
        }, () -> {
            String rawPassword = "123456";
            User nu = new User(displayName, passwordEncoder.encode(rawPassword), displayName, displayName, username);
            nu.setRole(role);
            userDao.save(nu);
            log.info("Usuario de prueba '{}' creado con rol {} (contraseña: {}).", username, role, rawPassword);
        });
    }
}
