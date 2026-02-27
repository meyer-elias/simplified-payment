package com.eliasmeyer.sp.domain.model.usuario;

/**
 * Factory para criação de usuários (Comum ou Lojista).
 * <p>
 * Responsável por instanciar o tipo correto de usuário baseado no tipo de documento, evitando uso
 * de instanceof na aplicação.
 */
public class UsuarioFactory {

	private UsuarioFactory() {
	}

	/**
	 * Cria um usuário baseado no tipo de documento.
	 *
	 * @param documento documento do usuário (CPF ou CNPJ)
	 * @param nome      nome do usuário
	 * @param email     email do usuário
	 * @param senha     senha hasheada do usuário
	 * @return instância de UsuarioComum ou Lojista
	 * @throws IllegalArgumentException se o documento é de tipo inválido
	 */
	public static Usuario criar(Documento documento, Nome nome, Email email, String senha) {
		if (documento instanceof Cpf cpf) {
			return new UsuarioComum(cpf, nome, email, senha);
		} else if (documento instanceof Cnpj cnpj) {
			return new Lojista(cnpj, nome, email, senha);
		}
		throw new IllegalArgumentException(
			"Tipo de documento inválido: " + documento.getClass().getSimpleName());
	}
}

