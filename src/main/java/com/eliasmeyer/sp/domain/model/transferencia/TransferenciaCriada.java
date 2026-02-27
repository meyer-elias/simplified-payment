package com.eliasmeyer.sp.domain.model.transferencia;

class TransferenciaCriada implements TransferenciaState {

	@Override
	public void reservar(Transferencia transferencia) {
		transferencia.mudarState(new TransferenciaReservada());
	}

	@Override
	public void completar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode realizar transferência antes de reservar!");
	}

	@Override
	public void cancelar(Transferencia transferencia) {
		throw new IllegalStateException("Não pode cancelar transferência antes de reservar!");
	}

	@Override
	public void falhar(Transferencia transferencia) {
		transferencia.mudarState(new TransferenciaFalhada());
	}
}
