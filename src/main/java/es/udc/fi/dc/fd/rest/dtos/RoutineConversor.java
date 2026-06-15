package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;

// Conversor entre Routine y RoutineDto (básico)
public class RoutineConversor {

    private RoutineConversor() {}

    // NOTE: Basic conversor (legacy) without exercise count / coach name.
    public static final RoutineDto toRoutineDto(Routine routine) {
        return new RoutineDto(routine.getId(), routine.getName(), routine.getUser() != null ? routine.getUser().getId() : null,
                routine.getUser() != null ? routine.getUser().getNombreUsuario() : null, null);
    }

    public static final List<RoutineDto> toRoutineDtos(List<Routine> routines) {
        return routines.stream().map(p -> toRoutineDto(p)).collect(Collectors.toList());
    }

    public static final Routine toRoutine(RoutineDto dto, User user) {
        Routine routine = new Routine(dto.getName(), user);
        routine.setId(dto.getId());
        return routine;
    }
}
