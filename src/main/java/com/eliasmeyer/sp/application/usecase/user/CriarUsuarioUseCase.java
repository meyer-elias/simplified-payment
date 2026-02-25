package com.eliasmeyer.sp.application.usecase.user;

import com.eliasmeyer.sp.application.port.user.in.CriarUsuarioCommand;
import com.eliasmeyer.sp.application.port.user.in.CriarUsuarioInputPort;
import com.eliasmeyer.sp.application.port.user.out.UsuarioOutputPort;
import com.eliasmeyer.sp.domain.model.usuario.*;

/**
 * Caso de uso para criar um novo usuário.
 * <p>
 * Responsável por:
 * - Validar entrada (comando)
 * - Verificar duplicatas (documento e email)
 * - Criar o usuário apropriado (Comum ou Lojista)
 * - Persistir o usuário
 */
public class CriarUsuarioUseCase implements CriarUsuarioInputPort {

    private final UsuarioOutputPort usuarioOutputPort;
    private final PasswordEncoder passwordEncoder;

    public CriarUsuarioUseCase(UsuarioOutputPort usuarioOutputPort, PasswordEncoder passwordEncoder) {
        this.usuarioOutputPort = usuarioOutputPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(CriarUsuarioCommand comando) {
        // Cria e valida documento
        Documento documento = DocumentoFactory.criar(comando.documento());

        // Valida e cria outros value objects
        Email email = new Email(comando.email());
        Nome nome = new Nome(comando.nome());

        // Verifica unicidade de documento
        usuarioOutputPort.buscarPorDocumento(documento).ifPresent(u -> {
            throw new IllegalArgumentException("Documento já cadastrado no sistema");
        });

        // Verifica unicidade de email
        usuarioOutputPort.buscarPorEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        });

        // Cria novo usuário do tipo apropriado
        String senhaHasheada = passwordEncoder.encode(comando.senha());
        Usuario novoUsuario = UsuarioFactory.criar(documento, nome, email, senhaHasheada);

        // Persiste usuário
        usuarioOutputPort.registrar(novoUsuario);
    }
}

