package com.eliasmeyer.sp.domain.model.usuario;


public class UserComum extends User {

    public UserComum(Cpf cpf, Nome nome, Email email, String senha) {
        super(cpf, nome, email, senha, TipoUsuario.COMUM);
    }

    @Override
    public boolean canEnviarDinheiro() {
        return true;
    }
}
