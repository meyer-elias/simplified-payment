package com.eliasmeyer.sp.infrastructure.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.eliasmeyer.sp.core.domain.ports.out.PasswordEncoder;

/**
 * Implementação de PasswordEncoder usando BCrypt (at.favre.lib).
 */
public class BCryptPasswordEncoder implements PasswordEncoder {

	private final int strength;

	public BCryptPasswordEncoder() {
		this(10);
	}

	public BCryptPasswordEncoder(int strength) {
		this.strength = strength;
	}

	@Override
	public String encode(String rawPassword) {
		return BCrypt.withDefaults().hashToString(strength, rawPassword.toCharArray());
	}

	@Override
	public boolean matches(String rawPassword, String encodedPassword) {
		BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
		return result.verified;
	}
}
