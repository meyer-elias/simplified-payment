package com.eliasmeyer.sp.domain.ports.out;

/**
 * Interface para codificação de senhas.
 * <p>
 * Define contrato para diferentes estratégias de hash de senha. A implementação concreta pode usar
 * BCrypt, Argon2, etc.
 */
public interface PasswordEncoder {

	/**
	 * Codifica uma senha em texto plano.
	 *
	 * @param rawPassword senha em texto plano
	 * @return senha hasheada
	 */
	String encode(String rawPassword);

	/**
	 * Verifica se uma senha em texto plano corresponde ao hash.
	 *
	 * @param rawPassword     senha em texto plano
	 * @param encodedPassword senha hasheada
	 * @return true se as senhas correspondem
	 */
	boolean matches(String rawPassword, String encodedPassword);
}

