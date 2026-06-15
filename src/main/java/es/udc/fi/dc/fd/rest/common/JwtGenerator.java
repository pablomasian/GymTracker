package es.udc.fi.dc.fd.rest.common;

// Interfaz para generar y leer información de tokens JWT
public interface JwtGenerator {
	
	/**
	 * Genera un token JWT.
	 *
	 * @param info la información a codificar
	 * @return el token generado
	 */
	String generate(JwtInfo info);
	
	/**
	 * Obtiene la información decodificada desde un token JWT.
	 *
	 * @param token el token JWT
	 * @return la información decodificada
	 */
	JwtInfo getInfo(String token);

}
