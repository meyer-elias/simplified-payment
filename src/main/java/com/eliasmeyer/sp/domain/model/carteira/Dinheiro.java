package com.eliasmeyer.sp.domain.model.carteira;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Dinheiro implements Comparable<Dinheiro> {

    private BigDecimal valor;

    Dinheiro(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor não pode ser nulo");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor nao pode ser menor ou igual a zero");
        }

        this.valor = valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    public Dinheiro(String valor) {
        if (Objects.isNull(valor) || valor.isEmpty()) {
            throw new IllegalArgumentException("Valor é nulo ou está vazio");
        }
        this(new BigDecimal(valor));
    }

    public boolean isNegativoOuZero() {
        return this.valor.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isMenorQue(Dinheiro quantia) {
        return this.valor.compareTo(quantia.valor) < 0;
    }

    public boolean isMaiorOuIgual(Dinheiro quantia) {
        return this.valor.compareTo(quantia.valor) >= 0;
    }

    public void subtrair(Dinheiro quantia) {
        Objects.requireNonNull(quantia, "quantia nao pode ser nulo.");
        this.valor = valor.subtract(quantia.valor).setScale(2, RoundingMode.HALF_EVEN);
    }

    public void somar(Dinheiro quantia) {
        Objects.requireNonNull(quantia, "quantia nao pode ser nulo.");
        this.valor = valor.add(quantia.valor).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public int compareTo(Dinheiro o) {
        return valor.compareTo(o.valor);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Dinheiro dinheiro)) return false;

        return valor.equals(dinheiro.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return "Dinheiro{" + "valor= R$ " + valor + '}';
    }
}
