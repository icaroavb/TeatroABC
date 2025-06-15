// Pacote: com.teatroabc.infraestrutura.config
package com.teatroabc.infraestrutura.config;

import java.util.List;

public class TeatroLayoutConfig {
    private final List<SecaoConfig> secoes;

    public TeatroLayoutConfig(List<SecaoConfig> secoes) {
        this.secoes = secoes;
    }

    public List<SecaoConfig> getSecoes() {
        return secoes;
    }
}