package es.udc.fi.dc.fd.model.common.exceptions;

/**
 * Excepción para instancias no encontradas.
 */
@SuppressWarnings("serial")
public class InstanceNotFoundException extends InstanceException {
    
    /**
     * Instancia una nueva excepción de instancia no encontrada.
     *
     * @param name nombre de la entidad
     * @param key clave de la entidad
     */
    public InstanceNotFoundException(String name, Object key) {
    	super(name, key); 	
    }

}
