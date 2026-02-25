package com.eliasmeyer.sp.domain.model.usuario;

/**
 * Usuário do tipo lojista identificado por CNPJ.
 * <p>
 * Lojas têm permissão para receber dinheiro através do sistema,
 * mas NÃO podem enviar dinheiro para outras contas.
 */
public class Lojista extends Usuario {

    public Lojista(Cnpj cnpj, Nome nome, Email email, String senha) {
        super(cnpj, nome, email, senha, TipoUsuario.LOJISTA);
    }

    /**
     * Lojas não podem enviar dinheiro.
     */
    @Override
    public boolean canEnviarDinheiro() {
        return false;
    }
}

