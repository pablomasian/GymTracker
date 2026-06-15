package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.SetLog;

public class SetLogConversor {
    // Conversor entre SetLog y SetLogDto

    public static SetLogDto toSetLogDto(SetLog setLog) {
        if (setLog == null)
            return null;

        return new SetLogDto(
                setLog.getId(),
                setLog.getSession() != null ? setLog.getSession().getId() : null,
                setLog.getExercise() != null ? setLog.getExercise().getId() : null,
                setLog.getExercise() != null ? setLog.getExercise().getName() : null,
                setLog.getExercise() != null ? setLog.getExercise().getImageUrl() : null,
                setLog.getNumeroSerie(),
                setLog.getRepeticiones(),
                setLog.getPeso(),
                setLog.getDistancia(),
                setLog.getDuracion(),
                setLog.getExercise() != null && setLog.getExercise().getExerciseType() != null
                        ? setLog.getExercise().getExerciseType().name()
                        : "STRENGTH");
    }

    public static List<SetLogDto> toSetLogDtos(List<SetLog> setLogs) {
        return setLogs.stream()
                .map(SetLogConversor::toSetLogDto)
                .collect(Collectors.toList());
    }
}
