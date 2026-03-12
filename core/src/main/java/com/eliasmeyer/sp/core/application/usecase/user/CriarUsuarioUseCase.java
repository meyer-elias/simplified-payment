package com.eliasmeyer.sp.core.application.usecase.user;

import com.eliasmeyer.sp.core.application.exception.RegistradorUsuarioIndisponivelException;
import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraFactory;
import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.core.domain.model.usuario.Documento;
import com.eliasmeyer.sp.core.domain.model.usuario.DocumentoFactory;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Nome;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioFactory;
import com.eliasmeyer.sp.core.domain.ports.in.usuario.CriarUsuarioCommand;
import com.eliasmeyer.sp.core.domain.ports.in.usuario.CriarUsuarioInputPort;
import com.eliasmeyer.sp.core.domain.ports.out.PasswordEncoder;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.usuario.UsuarioOutputPort;

/**
 * Use case responsável por criar um usuário no sistema.
 *
 * @author Elias Meyer
 * @version 1.0
 * @since 1.0
 */
public class CriarUsuarioUseCase implements CriarUsuarioInputPort {

	private final UsuarioOutputPort usuarioOutputPort;

	private final CarteiraOutputPort carteiraOutputPort;

	private final PasswordEncoder passwordEncoder;

	private final AppTransactionManager appTransactionManager;

	public CriarUsuarioUseCase(UsuarioOutputPort usuarioOutputPort,
		CarteiraOutputPort carteiraOutputPort,
		PasswordEncoder passwordEncoder,
		AppTransactionManager appTransactionManager) {
		this.usuarioOutputPort = usuarioOutputPort;
		this.carteiraOutputPort = carteiraOutputPort;
		this.passwordEncoder = passwordEncoder;
		this.appTransactionManager = appTransactionManager;
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

		Carteira carteira = CarteiraFactory.criar(novoUsuario,
			new Dinheiro(comando.valorInicial()));

		appTransactionManager.execute(() -> {
			try {
				// Persiste
				usuarioOutputPort.salvar(novoUsuario);
				carteiraOutputPort.salvar(carteira);
			} catch (Exception ex) {
				throw new RegistradorUsuarioIndisponivelException("Erro ao registrar usuário.", ex);
			}
		});
	}
}

