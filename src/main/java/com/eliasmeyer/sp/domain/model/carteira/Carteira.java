package com.eliasmeyer.sp.domain.model.carteira;


public class Carteira {

    private final Dinheiro saldoDisponivel;
    private final Dinheiro saldoReservado;

    public Carteira() {
        this.saldoDisponivel = new Dinheiro("0");
        this.saldoReservado = new Dinheiro("0");
    }

    public Dinheiro saldo() {
        return saldoDisponivel;
    }

    public void creditar(Dinheiro quantia) {
        saldoDisponivel.somar(quantia);
    }

    public void reservar(Dinheiro quantia) {
        if (!temSaldo(quantia)) {
            throw new SaldoInsuficienteException("Saldo não disponível para reservar");
        }

        saldoDisponivel.subtrair(quantia);
        saldoReservado.somar(quantia);
    }

    public void confirmarReserva(Dinheiro quantia) {
        saldoReservado.subtrair(quantia);
    }

    public void cancelarReserva(Dinheiro quantia) {
        saldoDisponivel.somar(quantia);
        saldoReservado.subtrair(quantia);
    }

    public boolean temSaldo(Dinheiro quantia) {
        return this.saldoDisponivel.isMaiorOuIgual(quantia);
    }

    public Dinheiro getSaldoDisponivel() {
        return saldoDisponivel;
    }
}
