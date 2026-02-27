package com.eliasmeyer.sp.domain.model.usuario;

/**
 * Representa um CNPJ (Cadastro Nacional da Pessoa Jurídica) brasileiro. Valida número de dígitos,
 * formato e verifica dígitos verificadores.
 */
public class Cnpj extends Documento {

	Cnpj(String value) {
		super(validate(value));
	}

	/**
	 * Formata o CNPJ para o padrão: XX.XXX.XXX/XXXX-XX
	 */
	private static String format(String cnpj) {
		return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
	}

	/**
	 * Valida a sequência de verificação do CNPJ.
	 */
	private static boolean isValid(String cnpj) {
		// Rejeita CNPJ com todos os dígitos iguais
		if (cnpj.matches("(\\d)\\1{13}")) {
			return false;
		}

		// Valida primeiro dígito verificador
		int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
		int sum = 0;
		for (int i = 0; i < 12; i++) {
			sum += (cnpj.charAt(i) - '0') * weights1[i];
		}
		int firstDigit = 11 - (sum % 11);
		if (firstDigit > 9) {
			firstDigit = 0;
		}

		// Valida segundo dígito verificador
		int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
		sum = 0;
		for (int i = 0; i < 13; i++) {
			sum += (cnpj.charAt(i) - '0') * weights2[i];
		}
		int secondDigit = 11 - (sum % 11);
		if (secondDigit > 9) {
			secondDigit = 0;
		}

		// Compara dígitos calculados com os fornecidos
		return cnpj.charAt(12) - '0' == firstDigit &&
			cnpj.charAt(13) - '0' == secondDigit;
	}

	/**
	 * Valida e formata o CNPJ.
	 */
	private static String validate(String digits) {
		if (!isValid(digits)) {
			throw new IllegalArgumentException(
				"CNPJ inválido: dígitos verificadores não correspondem");
		}

		return digits;
	}

	@Override
	public String getNumero() {
		return format(this.numero);
	}
}
