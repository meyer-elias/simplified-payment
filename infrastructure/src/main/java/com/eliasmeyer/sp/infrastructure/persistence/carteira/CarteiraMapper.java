package com.eliasmeyer.sp.infrastructure.persistence.carteira;

import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraReconstituicao;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarteiraMapper {

	public Carteira toDomain(CarteiraEntity entity) {
		return new CarteiraReconstituicao(entity.getId(),
			entity.getUsuarioId(),
			entity.getSaldoDisponivel(),
			entity.getSaldoReservado(),
			entity.getTipoConta(),
			entity.getUltimaAtualizacao()).reconstituir();
	}

	public CarteiraEntity toEntity(Carteira carteira) {
		CarteiraEntity entity = new CarteiraEntity();
		entity.setId(carteira.getId().asString());
		entity.setUsuarioId(carteira.getUsuarioId().asString());
		entity.setSaldoDisponivel(carteira.getSaldoDisponivel().getValor());
		entity.setSaldoReservado(carteira.getSaldoReservado().getValor());
		entity.setTipoConta(carteira.getTipoConta());
		entity.setUltimaAtualizacao(carteira.getUltimaAtualizacao());
		return entity;
	}
}
