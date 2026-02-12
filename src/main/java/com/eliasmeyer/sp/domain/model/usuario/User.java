package com.eliasmeyer.sp.domain.model.usuario;


import com.eliasmeyer.sp.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.shared.Entity;

import java.util.Objects;

public abstract class User extends Entity<UserId> {

    protected final UserId id;

    protected final Documento documento;

    protected Nome nome;

    protected Email email;

    protected String senha;

    protected Carteira carteira;

    protected TipoUsuario tipo;

    protected User(Documento documento, Nome nome, Email email, String senha, TipoUsuario tipo) {
        super(new UserId());

        if (Objects.isNull(documento)) {
            throw new IllegalArgumentException("documento é mandatório");
        }

        if (Objects.isNull(nome)) {
            throw new IllegalArgumentException("nome é mandatório");
        }

        if (Objects.isNull(email)) {
            throw new IllegalArgumentException("email é mandatório");
        }

        if (Objects.isNull(senha)) {
            throw new IllegalArgumentException("senha é mandatório");
        }

        if (Objects.isNull(tipo)) {
            throw new IllegalArgumentException("tipo é mandatório");
        }

        this.id = new UserId();
        this.documento = documento;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.carteira = new Carteira(this.getId());
    }

    public abstract boolean canEnviarDinheiro();

    public Email getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void receber(Dinheiro quantia) {
        this.getCarteira().creditar(quantia);
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public boolean isComum() {
        return tipo == TipoUsuario.COMUM;
    }

    public boolean isLojista() {
        return tipo == TipoUsuario.LOJISTA;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        if (!super.equals(o)) return false;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
