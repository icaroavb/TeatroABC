package com.teatroabc.dominio.validadores;

/**
 * Utilitário para validar a estrutura e os dígitos verificadores de um número de CPF.
 * Esta classe contém lógica de domínio pura relacionada à regra de formação de um CPF brasileiro.
 */
public class ValidadorCPF {

    /**
     * Verifica se uma string de CPF fornecida é válida de acordo com o algoritmo padrão.
     * A string de CPF de entrada DEVE conter apenas dígitos.
     *
     * @param cpf String contendo 11 dígitos do CPF (sem formatação).
     * @return {@code true} se o CPF for válido, {@code false} caso contrário.
     *         Retorna {@code false} também se a entrada for nula, tiver tamanho incorreto
     *         ou contiver apenas dígitos repetidos.
     */
    public static boolean isValid(String cpf) {
        // Considera-se CPF's formados por uma sequência de números iguais como inválidos.
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int)(cpf.charAt(i) - 48); // (48 é a posição de '0' na tabela ASCII)
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char)(r + 48); // Converte no respectivo caractere numérico
            }

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i = 0; i < 10; i++) {
                num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char)(r + 48);
            }

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));

        } catch (Exception e) { // InputMismatchException é para Scanner, aqui seria mais IndexOutOfBounds ou similar
            // Se ocorrer qualquer exceção durante o cálculo (ex: string não numérica, embora o regex já ajude),
            // considera-se inválido.
            // System.err.println("Erro ao validar CPF: " + cpf + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Normaliza uma string de CPF, removendo caracteres não numéricos.
     * @param cpf A string de CPF a ser normalizada.
     * @return O CPF contendo apenas dígitos, ou a string original se for nula.
     *         Retorna uma string vazia se o CPF original for uma string vazia após a remoção.
     */
    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Formata um CPF (apenas dígitos) para o padrão XXX.XXX.XXX-XX.
     * @param cpfNormalizado O CPF contendo 11 dígitos.
     * @return O CPF formatado, ou a string original se não tiver 11 dígitos.
     */
    public static String formatarParaExibicao(String cpfNormalizado) {
        if (cpfNormalizado != null && cpfNormalizado.length() == 11) {
            return cpfNormalizado.substring(0, 3) + "." +
                    cpfNormalizado.substring(3, 6) + "." +
                    cpfNormalizado.substring(6, 9) + "-" +
                    cpfNormalizado.substring(9, 11);
        }
        return cpfNormalizado; // Retorna original se não puder formatar
    }
}