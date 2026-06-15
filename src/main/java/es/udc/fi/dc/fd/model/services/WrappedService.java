package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.rest.dtos.WrappedDto;

/**
 * Servicio para calcular las estadísticas del Wrapped anual
 */
public interface WrappedService {

    /**
     * Obtiene el resumen anual (Wrapped) de un usuario
     * 
     * @param userId ID del usuario
     * @param year   Año del resumen
     * @return DTO con todas las estadísticas del año
     */
    WrappedDto getWrapped(Long userId, int year);
}
