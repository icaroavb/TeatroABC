package com.teatroabc.dominio.fidelidade;

import java.math.BigDecimal;


/**
 * Implementação do PlanoFidelidade para membros "ABC GOLD".
 * Oferece um percentual de desconto nas compras.
 */
public class MembroABCGold implements PlanoFidelidade {

    public static final String IDENTIFICADOR = "GOLD";
    private static final String NOME_PLANO = "ABC GOLD";
    private static final BigDecimal FATOR_DESCONTO_GOLD = new BigDecimal("0.05"); // 5%
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
    public BigDecimal getFatorDesconto() {
        return FATOR_DESCONTO_GOLD;
    }

    @Override
    public String getDescricaoBeneficios() {
        return DESCRICAO_BENEFICIOS;
    }
}