package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.model.transferencia.TransferenciaReconstituicao;
import com.eliasmeyer.sp.infrastructure.persistence.carteira.CarteiraMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class TransferenciaMapper {

	private final CarteiraMapper carteiraMapper;

	@Inject
	public TransferenciaMapper(CarteiraMapper carteiraMapper) {
		this.carteiraMapper = carteiraMapper;
	}

	public Transferencia toDomain(TransferenciaEntity entity) {

		return new TransferenciaReconstituicao(
			entity.getId(),
			carteiraMapper.toDomain(entity.getCarteiraPagador()),
			carteiraMapper.toDomain(entity.getCarteiraRecebedor()),
			new Dinheiro(entity.getValor()),
			entity.getCriadoEm(),
			entity.getAtualizadoEm(),
			entity.getStatus()
		).reconstituir();
	}

	public TransferenciaEntity toEntity(Transferencia transferencia) {
		TransferenciaEntity entity = new TransferenciaEntity();
		entity.setId(transferencia.getId().asString());
		entity.setCarteiraPagador(carteiraMapper.toEntity(transferencia.getPagador()));
		entity.setCarteiraRecebedor(carteiraMapper.toEntity(transferencia.getRecebedor()));
		entity.setStatus(transferencia.getStatus());
		entity.setValor(transferencia.getQuantia().getValor());
		entity.setAtualizadoEm(transferencia.getAtualizadoEm());
		entity.setCriadoEm(transferencia.getCriadoEm());
		return entity;
	}

}
