package com.eliasmeyer.sp.domain.model.usuario;

/**
 * Factory para criação de documentos (CPF ou CNPJ).
 * <p>
 * Responsável por identificar o tipo de documento a partir do número
 * e instanciar a classe apropriada, evitando uso de instanceof.
 */
public class DocumentoFactory {

    private static final int CPF_LENGTH = 11;
    private static final int CNPJ_LENGTH = 14;

    private DocumentoFactory() {
    }

    /**
     * Cria um documento a partir de um número.
     *
     * @param numeroDocumento número do documento (com ou sem formatação)
     * @return instância de CPF ou CNPJ
     * @throws IllegalArgumentException se o documento é inválido
     */
    public static Documento criar(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new IllegalArgumentException("Número de documento é obrigatório");
        }

        String digits = removeNonDigits(numeroDocumento);

        if (digits.isBlank()) {
            throw new IllegalArgumentException("Documento inválido: nenhum dígito encontrado");
        }

        return switch (digits.length()) {
            case CPF_LENGTH -> new Cpf(numeroDocumento);
            case CNPJ_LENGTH -> new Cnpj(numeroDocumento);
            default -> throw new IllegalArgumentException(
                    String.format("Documento inválido: deve ter %d (CPF) ou %d (CNPJ) dígitos, mas tem %d",
                            CPF_LENGTH, CNPJ_LENGTH, digits.length())
            );
        };
    }

    /**
     * Remove caracteres não numéricos.
     */
    private static String removeNonDigits(String value) {
        return value.replaceAll("\\D", "");
    }
}

