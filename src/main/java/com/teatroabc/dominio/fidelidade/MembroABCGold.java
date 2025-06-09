package com.teatroabc.dominio.fidelidade;

import com.teatroabc.dominio.modelos.Assento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implementação do PlanoFidelidade para membros "ABC GOLD".
 * Oferece um percentual de desconto nas compras.
 */
public class MembroABCGold implements PlanoFidelidade {

    public static final String IDENTIFICADOR = "GOLD";
    private static final String NOME_PLANO = "ABC GOLD";
    private static final BigDecimal PERCENTUAL_DESCONTO = new BigDecimal("0.05"); // 5%
    private static final String DESCRICAO_BENEFICIOS = "Desconto de 5% em todas as compras e ofertas exclusivas!";

    @Override
    public String getNomePlano() {
        return NOME_PLANO;
    }

    @Override
    public String getIdentificadorPlano() {
        return IDENTIFICADOR;
    }

    @Override
    public BigDecimal calcularDesconto(List<Assento> assentos) {
        if (assentos == null || assentos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Assento assento : assentos) {
            // Certifica-se de que o preço do assento não é nulo
            if (assento != null && assento.getPreco() != null) {
                subtotal = subtotal.add(assento.getPreco());
            }
        }

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return subtotal.multiply(PERCENTUAL_DESCONTO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescricaoBeneficios() {
        return DESCRICAO_BENEFICIOS;
    }

    // Exemplo, se implementado na interface:
    // @Override
    // public boolean requerTelefone() {
    //     return true; // Telefone é obrigatório para ser membro ABC Gold
    // }

    // @Override
    // public boolean requerEmail() {
    //     return true; // Email é obrigatório para ser membro ABC Gold
    // }
}