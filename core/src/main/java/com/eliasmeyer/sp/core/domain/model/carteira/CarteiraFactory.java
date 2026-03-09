package com.eliasmeyer.sp.core.domain.model.carteira;

import com.eliasmeyer.sp.core.domain.model.usuario.Lojista;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioComum;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import java.time.LocalDateTime;

public class CarteiraFactory {

	private CarteiraFactory() {
	}

	/**
	 * Cria carteira baseada no tipo do usuário usando pattern matching.
	 *
	 * @throws IllegalArgumentException se tipo do usuário é inválido
	 */
	public static Carteira criar(Usuario usuario, Dinheiro saldoInicialDisponivel) {
		return switch (usuario) {
			case UsuarioComum comum ->
				new CarteiraComum(comum.getUsuarioId(), saldoInicialDisponivel);
			case Lojista lojista ->
				new CarteiraLojista(lojista.getUsuarioId(), saldoInicialDisponivel);
			default -> throw new IllegalArgumentException(
				"Tipo de usuário inválido: " + usuario.getClass().getSimpleName());
		};
	}


	public static Carteira reconstituir(
		CarteiraId carteiraId,
		UsuarioId usuarioId,
		TipoConta tipoConta,
		Dinheiro saldoDisponivel,
		Dinheiro saldoReservado,
		LocalDateTime ultimaAtualizacao
	) {
		return switch (tipoConta) {
			case COMUM -> new CarteiraComum(carteiraId, usuarioId, saldoDisponivel, saldoReservado,
				ultimaAtualizacao);
			case LOJISTA ->
				new CarteiraLojista(carteiraId, usuarioId, saldoDisponivel, saldoReservado,
					ultimaAtualizacao);
		};
	}

}
