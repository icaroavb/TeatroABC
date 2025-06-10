package com.teatroabc.dominio.fidelidade;

import java.math.BigDecimal;


/**
 * Implementação do PlanoFidelidade para clientes sem nenhum plano especial.
 * Não oferece descontos.
 */
public class SemFidelidade implements PlanoFidelidade {

    public static final String IDENTIFICADOR = "PADRAO";
    private static final String NOME_PLANO = "Padrão";
    private static final String DESCRICAO_BENEFICIOS = "Nenhum benefício especial de fidelidade.";

    @Override
    public String getNomePlano() {
        return NOME_PLANO;
    }

    @Override
    public String getIdentificadorPlano() {
        return IDENTIFICADOR;
    }

    public BigDecimal getFatorDesconto() {
        return BigDecimal.ZERO; // 0% de desconto
    }

    @Override
    public String getDescricaoBeneficios() {
        return DESCRICAO_BENEFICIOS;
    }

    // Exemplo, se implementado na interface:
    // @Override
    // public boolean requerTelefone() {
    //     return false; // Telefone não é estritamente obrigatório para o plano padrão
    // }

    // @Override
    // public boolean requerEmail() {
    //     return false; // Email não é estritamente obrigatório para o plano padrão
    // }
}