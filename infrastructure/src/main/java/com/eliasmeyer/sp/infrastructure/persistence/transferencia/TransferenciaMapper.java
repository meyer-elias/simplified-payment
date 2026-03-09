package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.model.transferencia.TransferenciaReconstituicao;

class TransferenciaMapper {

	public Transferencia toDomain(TransferenciaEntity entity, Carteira carteiraPagador,
		Carteira recebedorCarteira) {

		return new TransferenciaReconstituicao(
			entity.getId(),
			carteiraPagador,
			recebedorCarteira,
			new Dinheiro(entity.getValor()),
			entity.getCriadoEm(),
			entity.getAtualizadoEm(),
			entity.getStatus()
		).reconstituir();
	}

	public TransferenciaEntity toEntity(Transferencia transferencia) {
		TransferenciaEntity entity = new TransferenciaEntity();
		entity.setId(transferencia.getId().asString());
		entity.setCarteiraIdPagador(transferencia.getPagador().getId().asString());
		entity.setCarteiraIdRecebedor(transferencia.getRecebedor().getId().asString());
		entity.setStatus(transferencia.getStatus());
		entity.setValor(transferencia.getQuantia().getValor());
		entity.setAtualizadoEm(transferencia.getAtualizadoEm());
		return entity;
	}

}
