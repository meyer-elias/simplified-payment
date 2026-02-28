package com.eliasmeyer.sp.application.usecase.user;

import com.eliasmeyer.sp.application.exception.RegistradorUsuarioIndisponivelException;
import com.eliasmeyer.sp.application.ports.TransactionManager;
import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.usuario.Documento;
import com.eliasmeyer.sp.domain.model.usuario.DocumentoFactory;
import com.eliasmeyer.sp.domain.model.usuario.Email;
import com.eliasmeyer.sp.domain.model.usuario.Nome;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioFactory;
import com.eliasmeyer.sp.domain.ports.in.usuario.CriarUsuarioCommand;
import com.eliasmeyer.sp.domain.ports.in.usuario.CriarUsuarioInputPort;
import com.eliasmeyer.sp.domain.ports.out.PasswordEncoder;
import com.eliasmeyer.sp.domain.ports.out.usuario.UsuarioOutputPort;

/**
 * Caso de uso para criar um novo usuário.
 * <p>
 * Responsável por: - Validar entrada (comando) - Verificar duplicatas (documento e email) - Criar o
 * usuário apropriado (Comum ou Lojista) - Persistir o usuário
 */
public class CriarUsuarioUseCase implements CriarUsuarioInputPort {

	private final UsuarioOutputPort usuarioOutputPort;
	private final PasswordEncoder passwordEncoder;
	private final AppLogger appLogger;
	private final TransactionManager transactionManager;

	public CriarUsuarioUseCase(UsuarioOutputPort usuarioOutputPort, PasswordEncoder passwordEncoder,
		AppLogger appLogger, TransactionManager transactionManager) {
		this.usuarioOutputPort = usuarioOutputPort;
		this.passwordEncoder = passwordEncoder;
		this.appLogger = appLogger;
		this.transactionManager = transactionManager;
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

		transactionManager.execute(() -> {
			try {
				// Persiste usuário
				usuarioOutputPort.salvar(novoUsuario);
			} catch (Exception ex) {
				appLogger.error("Erro ao registrar usuário.", ex);
				throw new RegistradorUsuarioIndisponivelException("Erro ao registrar usuário.", ex);
			}
			return null;
		});
	}
}

