package es.udc.fi.dc.fd.model.common.exceptions;

/**
 * Excepción para instancias duplicadas.
 */
@SuppressWarnings("serial")
public class DuplicateInstanceException extends InstanceException {

    /**
     * Instancia una nueva excepción de instancia duplicada.
     *
     * @param name nombre de la entidad
     * @param key clave de la entidad
     */
    public DuplicateInstanceException(String name, Object key) {
        super(name, key);
    }
    
}
