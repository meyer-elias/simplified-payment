package com.eliasmeyer.sp.core.domain.model.carteira;

import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import java.time.LocalDateTime;

public class CarteiraComum extends Carteira {

	CarteiraComum(UsuarioId usuarioId, Dinheiro saldoInicialDisponivel) {
		super(usuarioId, TipoConta.COMUM, saldoInicialDisponivel);
	}

	public CarteiraComum(CarteiraId carteiraId, UsuarioId usuarioId, Dinheiro saldoDisponivel,
		Dinheiro saldoReservado, LocalDateTime ultimaAtualizacao) {
		super(carteiraId, usuarioId, TipoConta.COMUM, saldoDisponivel, saldoReservado,
			ultimaAtualizacao);
	}
}
