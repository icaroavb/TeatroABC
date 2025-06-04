package com.teatroabc.fidelidade;

import com.teatroabc.modelos.Assento;
import java.math.BigDecimal;
import java.util.List;

public class MembroABCGold implements PlanoFidelidade {
    public static final String IDENTIFICADOR = "GOLD";
    private static final BigDecimal PERCENTUAL_DESCONTO = new BigDecimal("0.05"); // 5% de desconto como desconto padrão

    @Override
    public String getNomePlano() {
        return "ABC GOLD";
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
            subtotal = subtotal.add(BigDecimal.valueOf(assento.getPreco()));
        }
        return subtotal.multiply(PERCENTUAL_DESCONTO).setScale(2, BigDecimal.ROUND_HALF_UP); // Arredondar para 2 casas decimais
    }

    @Override
    public String getDescricaoBeneficios() {
        return "5% de desconto em todas as compras!";
    }
}