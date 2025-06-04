package com.teatroabc.fidelidade;

import com.teatroabc.modelos.Assento;
import java.math.BigDecimal;
import java.util.List;

public class SemFidelidade implements PlanoFidelidade {
    public static final String IDENTIFICADOR = "PADRAO";

    @Override
    public String getNomePlano() {
        return "Padrão";
    }

    @Override
    public String getIdentificadorPlano() {
        return IDENTIFICADOR;
    }

    @Override
    public BigDecimal calcularDesconto(List<Assento> assentos) {
        return BigDecimal.ZERO;
    }

    @Override
    public String getDescricaoBeneficios() {
        return "Nenhum benefício especial.";
    }
}