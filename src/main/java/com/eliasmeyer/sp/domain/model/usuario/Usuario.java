package com.eliasmeyer.sp.domain.model.usuario;

import com.eliasmeyer.sp.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.shared.Entity;

import java.util.Objects;

/**
 * Entidade raiz de agregado que representa um usuário do sistema.
 * <p>
 * Usuários podem ser de dois tipos:
 * - UsuarioComum (CPF): pode enviar dinheiro
 * - Lojista (CNPJ): não pode enviar dinheiro
 * <p>
 * Cada usuário possui uma carteira para gerenciar saldo.
 */
public abstract class Usuario extends Entity<UsuarioId> {

    protected final UsuarioId id;
    protected final Documento documento;
    protected final Nome nome;
    protected final Email email;
    protected final String senha;
    protected final TipoUsuario tipo;
    protected final Carteira carteira;

    /**
     * Construtor protegido para criação de usuários.
     *
     * @param documento documento de identificação (CPF ou CNPJ)
     * @param nome      nome do usuário
     * @param email     email do usuário
     * @param senha     senha hasheada
     * @param tipo      tipo de usuário (COMUM ou LOJISTA)
     */
    protected Usuario(Documento documento, Nome nome, Email email, String senha, TipoUsuario tipo) {
        super(new UsuarioId());

        this.id = new UsuarioId();
        this.documento = Objects.requireNonNull(documento, "Documento não pode ser nulo");
        this.nome = Objects.requireNonNull(nome, "Nome não pode ser nulo");
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.senha = Objects.requireNonNull(senha, "Senha não pode ser nula");
        this.tipo = Objects.requireNonNull(tipo, "Tipo de usuário não pode ser nulo");
        this.carteira = new Carteira();
    }

    /**
     * Verifica se o usuário pode enviar dinheiro.
     * Implementado pelas subclasses.
     */
    public abstract boolean canEnviarDinheiro();

    public Documento getDocumento() {
        return documento;
    }

    public Nome getNome() {
        return nome;
    }

    public Email getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    /**
     * Adiciona dinheiro à carteira do usuário.
     */
    public void receber(Dinheiro quantia) {
        this.carteira.creditar(quantia);
    }

    /**
     * Verifica se o usuário é do tipo comum.
     */
    public boolean isComum() {
        return tipo == TipoUsuario.COMUM;
    }

    /**
     * Verifica se o usuário é lojista.
     */
    public boolean isLojista() {
        return tipo == TipoUsuario.LOJISTA;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Usuario usuario)) return false;
        if (!super.equals(o)) return false;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Usuario[id=%s, documento=%s, nome=%s, tipo=%s]",
                id, documento, nome, tipo);
    }
}
