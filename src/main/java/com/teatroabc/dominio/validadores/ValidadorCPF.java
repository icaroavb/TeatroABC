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
        // Considera-se CPF's formados por uma sequência de números iguais como inválidos - lógica dessa verificação foi encapsulada
        if (verificarDigitosRepetidos(cpf)) {
            return false;
        }

        char dig10, dig11; //recebem o respectivo códiog ASCII
        int soma, i, codigoASCI, num, peso; //variáveis auxiliares necessárias para fazer o cálculo

        try {
            // Calculo do 1o. Digito Verificador
            soma = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int)(cpf.charAt(i) - 48); // (48 é a posição de '0' na tabela ASCII)
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            codigoASCI = 11 - (soma % 11);
            if ((codigoASCI == 10) || (codigoASCI == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char)(codigoASCI + 48); // Converte no respectivo caractere numérico
            }

            // Calculo do 2o. Digito Verificador
            soma = 0;
            peso = 11;
            for(i = 0; i < 10; i++) {
                num = (int)(cpf.charAt(i) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            codigoASCI = 11 - (soma % 11);
            if ((codigoASCI == 10) || (codigoASCI == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char)(codigoASCI + 48);
            }

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            return verificacaoFinal(cpf, dig10, dig11);
            
        } catch (IndexOutOfBoundsException e) {
            // Captura exceção se o acesso a um índice da string falhar (embora as verificações iniciais já ajudem)
            // Isso pode ocorrer se 'cpf' tiver um tamanho inesperado em tempo de execução
            System.err.println("Quantidade de caracteres informados inválida.");
            return false;

        } catch (NumberFormatException e) {
            // Captura exceção se um caractere não puder ser convertido para um valor numérico válido.
            // Embora o Javadoc diga que a entrada DEVE conter apenas dígitos, este catch garante robustez.
            System.err.println("Não foi possível apurar a validade do número de CPF.");
            return false;

        }
    }

    /**
     * Encapsulamento da verificacao final
     * @param cpf
     * @param decimoDigito
     * @param decimoPrimeiroDigito
     * @return true se os digitos dos parâmetros coincidirem com os digitos presentes no cpf informado
     */
    public static boolean verificacaoFinal(String cpf, char decimoDigito, char decimoPrimeiroDigito){
        return decimoDigito == cpf.charAt(9) && decimoPrimeiroDigito == cpf.charAt(10);
    } 

    /**
     * Encapsulamento da lógica do blacklist - CPFs que são formados números repetidos somente
     * @param cpf
     * @return true se detectar um cpf que seja, por exemplo, 111.111.111-11
     */
    public static boolean verificarDigitosRepetidos (String cpf){
        return cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}");
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