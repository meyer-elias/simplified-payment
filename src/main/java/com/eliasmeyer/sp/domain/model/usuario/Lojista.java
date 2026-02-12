package com.eliasmeyer.sp.domain.model.usuario;


public class Lojista extends User {

    public Lojista(Cnpj cnpj, Nome nome, Email email, String senha) {
        super(cnpj, nome, email, senha, TipoUsuario.LOJISTA);
    }

    @Override
    public boolean canEnviarDinheiro() {
        return false;
    }
}

