package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.services.SocialService;


@Component
public class WorkoutSessionConversor {
    // Conversor entre WorkoutSession y su DTO

    @Autowired
    private SocialService socialService;

    private WorkoutSessionConversor() {}


    // en este conversor aunque no sea el usado en el feed y no sea necesario el campo liked, si se inicializa como null da un error, lo 
    // inicializaremos como false ya que esto no será visible desde el front ni afectará a BD
    public static final WorkoutSessionDto toWorkoutSessionDto(WorkoutSession session) {
        
        return new WorkoutSessionDto(
            session.getId(),
            session.getUser() != null ? session.getUser().getId() : null,
            session.getUser() != null ? session.getUser().getUsername() : null,
            session.getRoutine() != null ? session.getRoutine().getId() : null,
            session.getRoutine() != null ? session.getRoutine().getName() : null,
            session.getFecha(),
            session.getStartTime(),
            session.getEndTime(),
            false
        );
    }


    //este builder incluye liked para cuando se carga desde la feed saber si el usuario ha dado like a la sesión o no
    public final WorkoutSessionDto toWorkoutSessionDtoFeed(WorkoutSession session, Long currentUserId) {
        

        boolean liked = socialService.is_liked(currentUserId, session);


        return new WorkoutSessionDto(
            session.getId(),
            session.getUser() != null ? session.getUser().getId() : null,
            session.getUser() != null ? session.getUser().getUsername() : null,
            session.getRoutine() != null ? session.getRoutine().getId() : null,
            session.getRoutine() != null ? session.getRoutine().getName() : null,
            session.getFecha(),
            session.getStartTime(),
            session.getEndTime(),
            liked
        );
    }

    public static final List<WorkoutSessionDto> toWorkoutSessionDtos(List<WorkoutSession> sessions) {
        return sessions.stream().map(s -> toWorkoutSessionDto(s)).collect(Collectors.toList());
    }

    public final List<WorkoutSessionDto> toWorkoutSessionDtosFeed(
        List<WorkoutSession> sessions, Long currentUserId) {

        return sessions.stream()
                   .map(s -> toWorkoutSessionDtoFeed(s, currentUserId))
                   .collect(Collectors.toList());
    }


    public static final WorkoutSession toWorkoutSession(WorkoutSessionDto dto, User user, Routine routine) {
        WorkoutSession session = new WorkoutSession(user, routine, dto.getFecha());
        session.setId(dto.getId());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        return session;
    }
}
