package com.eliasmeyer.sp.domain.model.usuario;

import java.util.Objects;

public class Cnpj extends Documento {

    public Cnpj(String value) {
        super(value);
    }

    /**
     * Remove non digits
     *
     * @param cnpj
     * @return cnpj only digits
     */
    private static String removeNonDigits(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }

    /**
     * Validate cnpj number
     *
     * @param cnpj only digits and length equal 14
     * @return true is valid
     */
    private static boolean isValid(String cnpj) {
        // First verification digit
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * weights1[i];
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;

        // Second verification digit
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * weights2[i];
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;

        // Check if calculated digits match the provided ones
        return cnpj.charAt(12) - '0' == firstDigit &&
                cnpj.charAt(13) - '0' == secondDigit;

    }

    @Override
    public String getNumero() {
        return this.numero.replace("/^(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})$/,", "$1.$2.$3/$4-$5");
    }

    @Override
    protected String validate(String value) {
        addValidation(Objects::isNull, "Cnpj não pode ser nulo!");
        addValidation(String::isBlank, "Cnpj não pode ser branco!");
        //Check if it has char
        addValidation(v -> !v.replaceAll("\\D", "").isBlank(), "Cnpj inválido - Letras presentes!");

        // Check length number is equals 14 digits
        addValidation(v -> {
            var cnpjClean = removeNonDigits(v);
            return !cnpjClean.isBlank() && !cnpjClean.matches("\\d{14}");
        }, "Cnpj inválido - Comprimento do cnpj é inválido");

        // Check if it's sequence digits
        addValidation(v -> v.matches("(\\d)\\1{13}"), "Cnpj inválido - Sequência repetida!");

        //Check if number is valid
        addValidation(v -> isValid(removeNonDigits(v)), "Cnpj inválido - dígitos verificadores não correspondem");
        return value;
    }
}
