package com.eliasmeyer.sp.core.domain.model.carteira;

import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class CarteiraReconstituicao {

	private final CarteiraId carteiraId;

	private final UsuarioId usuarioId;

	private final Dinheiro saldoDisponivel;

	private final Dinheiro saldoReservado;

	private final TipoConta tipoConta;

	private final LocalDateTime ultimaAtualizacao;

	public CarteiraReconstituicao(String carteiraId, String usuarioId, BigDecimal saldoDisponivel,
		BigDecimal saldoReservado, TipoConta tipoConta, LocalDateTime ultimaAtualizacao) {
		this.carteiraId = new CarteiraId(carteiraId);
		this.usuarioId = new UsuarioId(usuarioId);
		this.saldoDisponivel = new Dinheiro(saldoDisponivel);
		this.saldoReservado = new Dinheiro(saldoReservado);
		this.tipoConta = tipoConta;
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public Carteira reconstituir() {
		return switch (tipoConta) {
			case COMUM -> new CarteiraComum(carteiraId, usuarioId, saldoDisponivel, saldoReservado,
				ultimaAtualizacao);
			case LOJISTA ->
				new CarteiraLojista(carteiraId, usuarioId, saldoDisponivel, saldoReservado,
					ultimaAtualizacao);
		};
	}
}
