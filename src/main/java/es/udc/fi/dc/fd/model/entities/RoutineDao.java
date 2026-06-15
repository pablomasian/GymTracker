package es.udc.fi.dc.fd.model.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz del repositorio de rutinas.
 */
@Repository
public interface RoutineDao extends JpaRepository<Routine, Long> {

    /**
     * Busca todas las rutinas por usuario.
     *
     * @param user el usuario
     * @return la lista de rutinas
     */
    List<Routine> findByUser(User user);

    List<Routine> findByUser_Id(Long coachId);

    

    boolean existsByNameAndUser(String name, User user);

    List<Routine> findByEstadoAndVisibleTrue(Routine.RoutineEstado estado);
    
    List<Routine> findByEstado(Routine.RoutineEstado estado);

    List<Routine> findByUserAndVisible(User user, boolean visible);
}
