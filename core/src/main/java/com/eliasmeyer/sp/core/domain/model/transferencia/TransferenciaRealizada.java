package com.eliasmeyer.sp.core.domain.model.transferencia;

class TransferenciaRealizada implements TransferenciaState {

	@Override
	public void reservar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode reservar uma transferência que já foi realizada");
	}

	@Override
	public void completar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode realizar uma transferência que já foi realizada");

	}

	@Override
	public void cancelar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode cancelar uma transferência que já foi realizada");
	}

	@Override
	public void falhar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode falhar uma transferência que já foi realizada");
	}
}
