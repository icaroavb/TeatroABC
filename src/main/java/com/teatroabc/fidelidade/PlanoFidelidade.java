package com.teatroabc.fidelidade;

import com.teatroabc.modelos.Assento; // Para calcular com base nos itens da compra
import java.math.BigDecimal;
import java.util.List;

public interface PlanoFidelidade {
    String getNomePlano(); // Ex: "Padrão", "ABC GOLD"
    String getIdentificadorPlano(); // Ex: "PADRAO", "GOLD" - para persistência
    BigDecimal calcularDesconto(List<Assento> assentos);
    String getDescricaoBeneficios(); // Para exibir ao usuário

    // Opcional: poderia ter um método para indicar campos adicionais necessários
    // List<String> getCamposObrigatoriosAdicionais();
}