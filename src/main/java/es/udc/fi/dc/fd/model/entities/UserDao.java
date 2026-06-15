package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz del repositorio de usuarios.
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);
	Optional<User> findByUsername(String username);
	User getById(Long id);
	List<User> findByUsernameContainingIgnoreCase(String username);


}
