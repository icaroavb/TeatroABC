package com.teatroabc.utilitarios;

public class ValidadorCPF {
    // Validação simples de CPF
    public static boolean isCPF(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
        try {
            int d1 = 0, d2 = 0;
            for (int i = 0; i < 9; i++) {
                int dig = Character.getNumericValue(cpf.charAt(i));
                d1 += dig * (10 - i);
                d2 += dig * (11 - i);
            }
            int resto1 = d1 % 11;
            int digito1 = (resto1 < 2) ? 0 : 11 - resto1;
            d2 += digito1 * 2;
            int resto2 = d2 % 11;
            int digito2 = (resto2 < 2) ? 0 : 11 - resto2;
            return digito1 == Character.getNumericValue(cpf.charAt(9)) && digito2 == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValid(String cpf) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isValid'");
    }
}
