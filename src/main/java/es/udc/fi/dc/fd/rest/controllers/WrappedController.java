package es.udc.fi.dc.fd.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.udc.fi.dc.fd.model.services.WrappedService;
import es.udc.fi.dc.fd.rest.common.JwtInfo;
import es.udc.fi.dc.fd.rest.dtos.WrappedDto;

@RestController
@RequestMapping("/api/wrapped")
public class WrappedController {

    @Autowired
    private WrappedService wrappedService;

    /**
     * Obtiene el Wrapped (resumen anual) del usuario autenticado
     * 
     * @param year Año del resumen
     * @return DTO con todas las estadísticas
     */
    @GetMapping("/{year}")
    public WrappedDto getWrapped(
            @RequestAttribute("jwtInfo") JwtInfo jwtInfo,
            @PathVariable int year) {
        return wrappedService.getWrapped(jwtInfo.getUserId(), year);
    }

    /**
     * Obtiene el Wrapped del año actual
     * 
     * @return DTO con todas las estadísticas
     */
    @GetMapping("/current")
    public ResponseEntity<WrappedDto> getCurrentWrapped(@RequestAttribute("jwtInfo") JwtInfo jwtInfo) {
        java.time.LocalDate today = java.time.LocalDate.now();

        // Ventana de disponibilidad: desde 22 de diciembre del año de inicio
        // hasta 16 de enero del año siguiente (ambas fechas incluidas).
        // Calculamos el "wrappedYear" (el año al que pertenece el Wrapped)
        // y construimos la ventana en base a ese año para que funcione en
        // diciembre y en enero correctamente.
        int wrappedYearForWindow = (today.getMonthValue() == 1) ? today.getYear() - 1 : today.getYear();
        java.time.LocalDate start = java.time.LocalDate.of(wrappedYearForWindow, 12, 22);
        java.time.LocalDate end = java.time.LocalDate.of(wrappedYearForWindow + 1, 1, 16);

        // Si hoy está fuera de la ventana (incluyendo ambos extremos), devolvemos 204
        // No Content
        boolean inWindow = (!today.isBefore(start) && !today.isAfter(end));
        if (!inWindow) {
            return ResponseEntity.noContent().build();
        }

        // Cuando estamos en la ventana que cruza año (diciembre->enero) el Wrapped
        // corresponde al año de inicio (p. ej. diciembre 2025 y enero 2026 -> Wrapped
        // 2025).
        int wrappedYear = wrappedYearForWindow;

        WrappedDto dto = wrappedService.getWrapped(jwtInfo.getUserId(), wrappedYear);
        return ResponseEntity.ok(dto);
    }
}
