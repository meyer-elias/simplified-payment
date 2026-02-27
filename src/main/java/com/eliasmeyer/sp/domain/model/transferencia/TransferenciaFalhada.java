package com.eliasmeyer.sp.domain.model.transferencia;

class TransferenciaFalhada implements TransferenciaState {

	@Override
	public void falhar(Transferencia transferencia) {
		throw new IllegalStateException("Já falhada!");
	}

	@Override
	public void completar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode realizar transferência se está falhada!");
	}

	@Override
	public void cancelar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode cancelar transferência se está falhada!");
	}

	@Override
	public void reservar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode reservar transferência se está falhada!");
	}
}
