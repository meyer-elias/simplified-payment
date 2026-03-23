package com.eliasmeyer.sp.infrastructure.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.eliasmeyer.sp.core.domain.ports.out.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Implementação de PasswordEncoder usando BCrypt (at.favre.lib).
 */
@ApplicationScoped
public class BCryptPasswordEncoder implements PasswordEncoder {

	private final int strength;

	@Inject
	public BCryptPasswordEncoder(
		@ConfigProperty(name = "security.bcrypt.strength", defaultValue = "10")
		int strength) {
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
