package com.eliasmeyer.sp.domain.model.usuario;

import java.util.Objects;

public class Cpf extends Documento {

    public Cpf(String value) {
        super(value);
    }

    private static String removeNonDigits(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    private static boolean isValid(String value) {
        // All digits same check
        if (value.matches("(\\d)\\1{10}")) {
            return false;
        }

        // First verification digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (10 - i) * (value.charAt(i) - '0');
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;

        // Second verification digit
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (11 - i) * (i < 9 ? (value.charAt(i) - '0') : firstDigit);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;

        // Check if calculated digits match the provided ones
        return value.charAt(9) - '0' == firstDigit &&
                value.charAt(10) - '0' == secondDigit;
    }

    @Override
    public String getNumero() {
        return this.numero.replace("/(\\d{3})(\\d{3})(\\d{3})(\\d{2})/", "$1.$2.$3-$4");
    }

    @Override
    protected String validate(String value) {
        addValidation(Objects::isNull, "Cpf não pode ser nulo!");
        addValidation(String::isBlank, "Cpf não pode ser branco!");

        //Check if it has char
        addValidation(v -> !v.replaceAll("\\D", "").isBlank(), "Cpf inválido - Letras presentes!");

        //Check length number is equals 11 digits
        addValidation(v -> {
            var cnpjClean = removeNonDigits(v);
            return !cnpjClean.isBlank() && !cnpjClean.matches("\\d{11}");
        }, "Cpf inválido - Comprimento do cpf é inválido");

        // Check if is sequence digits
        addValidation(v -> v.matches("(\\d)\\1{10}"), "Cpf inválido - Sequência repetida!");

        //Check if number is valid
        addValidation(v -> isValid(removeNonDigits(v)), "Cpf inválido - dígitos verificadores não correspondem");
        return value;
    }
}

