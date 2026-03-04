package com.eliasmeyer.sp.core.domain.model.usuario;

/**
 * Representa um CPF (Cadastro de Pessoa Física) brasileiro. Valida número de dígitos, formato e
 * verifica dígitos verificadores.
 */
public class Cpf extends Documento {

	Cpf(String value) {
		super(validate(value));
	}

	/**
	 * Formata o CPF para o padrão: XXX.XXX.XXX-XX
	 */
	private static String format(String cpf) {
		return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}

	/**
	 * Valida a sequência de verificação do CPF.
	 */
	private static boolean isValid(String digits) {
		// Rejeita CPF com todos os dígitos iguais
		if (digits.matches("(\\d)\\1{10}")) {
			return false;
		}

		// Valida primeiro dígito verificador
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			sum += (10 - i) * (digits.charAt(i) - '0');
		}
		int firstDigit = 11 - (sum % 11);
		if (firstDigit > 9) {
			firstDigit = 0;
		}

		// Valida segundo dígito verificador
		sum = 0;
		for (int i = 0; i < 10; i++) {
			sum += (11 - i) * (i < 9 ? (digits.charAt(i) - '0') : firstDigit);
		}
		int secondDigit = 11 - (sum % 11);
		if (secondDigit > 9) {
			secondDigit = 0;
		}

		// Compara dígitos calculados com os fornecidos
		return digits.charAt(9) - '0' == firstDigit &&
			digits.charAt(10) - '0' == secondDigit;
	}

	/**
	 * Valida e formata o CPF.
	 */
	private static String validate(String digits) {
		if (!isValid(digits)) {
			throw new IllegalArgumentException(
				"CPF inválido: dígitos verificadores não correspondem");
		}

		return digits;
	}

	@Override
	public String getNumero() {
		return format(this.numero);
	}
}

