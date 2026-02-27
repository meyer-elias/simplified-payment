package com.eliasmeyer.sp.domain.model.transferencia;

class TransferenciaCancelada implements TransferenciaState {

	@Override
	public void falhar(Transferencia transferencia) {
		throw new IllegalStateException("Transferência já está cancelada!");
	}

	@Override
	public void cancelar(Transferencia transferencia) {
		throw new IllegalStateException("Transferência já está cancelada!");
	}

	@Override
	public void completar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode realizar transferência se está cancelada!");
	}

	@Override
	public void reservar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode reservar transferência se está cancelada!");
	}
}
