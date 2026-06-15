package es.udc.fi.dc.fd.model.common.exceptions;

/**
 * Excepción base para operaciones sobre instancias.
 */
@SuppressWarnings("serial")
public abstract class InstanceException extends Exception {

	/** Nombre de la entidad. */
	private final String name;

	/** Clave de la entidad. */
	private final transient Object key;

	/**
	 * Instancia una nueva excepción de instancia.
	 *
	 * @param name el nombre de la entidad
	 * @param key  la clave de la entidad
	 */
	protected InstanceException(String name, Object key) {
		this.name = name;
		this.key = key;
	}

	/**
	 * Devuelve el nombre de la entidad.
	 *
	 * @return el nombre
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuelve la clave de la entidad.
	 *
	 * @return la clave
	 */
	public Object getKey() {
		return key;
	}

}
