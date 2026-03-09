package com.eliasmeyer.sp.core.domain.model.carteira;

import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import java.time.LocalDateTime;

public class CarteiraLojista extends Carteira {

	CarteiraLojista(UsuarioId usuarioId, Dinheiro saldoInicialDisponivel) {
		super(usuarioId, TipoConta.LOJISTA, saldoInicialDisponivel);
	}

	public CarteiraLojista(CarteiraId carteiraId, UsuarioId usuarioId, Dinheiro saldoDisponivel,
		Dinheiro saldoReservado, LocalDateTime ultimaAtualizacao) {
		super(carteiraId, usuarioId, TipoConta.LOJISTA, saldoDisponivel, saldoReservado,
			ultimaAtualizacao);
	}
}
