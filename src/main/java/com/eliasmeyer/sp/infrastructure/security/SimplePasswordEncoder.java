package com.eliasmeyer.sp.infrastructure.security;

import com.eliasmeyer.sp.domain.ports.out.PasswordEncoder;

/**
 * Implementação simples de PasswordEncoder para desenvolvimento.
 * <p>
 * ATENÇÃO: Esta implementação é insegura e deve ser usada apenas para testes.
 * Para produção, use BCrypt ou Argon2.
 */
public class SimplePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        // Implementação placeholder para testes
        return "hashed_" + rawPassword + "_securely";
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        // Implementação placeholder para testes
        return encodedPassword.equals("hashed_" + rawPassword + "_securely");
    }
}

