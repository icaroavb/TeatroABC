package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;
import com.teatroabc.infraestrutura.utilitarios_comuns.GeradorIdUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação (Adaptador Secundário) do repositório de Sessões.
 * Atualmente, os dados das sessões são hardcoded para fins de demonstração.
 * Esta classe depende do IPecaRepositorio para obter os objetos Peca necessários
 * para construir as Sessões.
 */
public class SessaoRepositorio implements ISessaoRepositorio {

    private final IPecaRepositorio pecaRepositorio;
    private static final List<Sessao> sessoesDB = new ArrayList<>();

    /**
     * Construtor do SessaoRepositorio.
     * @param pecaRepositorio Uma implementação de IPecaRepositorio para buscar os dados das peças.
     */
    public SessaoRepositorio(IPecaRepositorio pecaRepositorio) {
        this.pecaRepositorio = pecaRepositorio;
        // O método que carrega os dados é chamado aqui para garantir que a lista seja preenchida na criação.
        carregarSessoesHardcoded();
    }

    /**
     * Método privado para popular a lista de sessões. Em um sistema real,
     * isso seria substituído por uma leitura de um banco de dados ou arquivo.
     */
    private void carregarSessoesHardcoded() {
        // Limpa a lista para evitar duplicação se o construtor for chamado mais de uma vez.
        if (!sessoesDB.isEmpty()) {
            return;
        }

        // Busca todas as peças disponíveis para podermos criar sessões para elas.
        List<Peca> todasAsPecas = pecaRepositorio.listarTodas();

        // Para cada peça, criamos algumas sessões de exemplo.
        for (Peca peca : todasAsPecas) {
            switch (peca.getTitulo()) {
                case "WICKEDONIA":
                    sessoesDB.add(new Sessao(GeradorIdUtil.gerarNovoId(), peca, LocalDateTime.of(2025, 6, 20, 20, 0), Turno.NOITE));
                    sessoesDB.add(new Sessao(GeradorIdUtil.gerarNovoId(), peca, LocalDateTime.of(2025, 6, 21, 17, 0), Turno.TARDE));
                    sessoesDB.add(new Sessao(GeradorIdUtil.gerarNovoId(), peca, LocalDateTime.of(2025, 6, 21, 20, 0), Turno.NOITE));
                    break;
                case "HERMANOTEU":
                    sessoesDB.add(new Sessao(GeradorIdUtil.gerarNovoId(), peca, LocalDateTime.of(2025, 6, 22, 17, 0), Turno.TARDE));
                    sessoesDB.add(new Sessao(GeradorIdUtil.gerarNovoId(), peca, LocalDateTime.of(2025, 6, 22, 20, 0), Turno.NOITE));
                    break;
                case "MORTE E VIDA SEVERINA":
                    sessoesDB.add(new Sessao(GeradorIdUtil.gerarNovoId(), peca, LocalDateTime.of(2025, 6, 28, 10, 0), Turno.MANHA));
                    sessoesDB.add(new Sessao(GeradorIdUtil.gerarNovoId(), peca, LocalDateTime.of(2025, 6, 28, 17, 0), Turno.TARDE));
                    break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sessao> buscarSessoesPorPeca(String idPeca) {
        if (idPeca == null || idPeca.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Filtra a lista de sessões em memória para encontrar aquelas que correspondem ao ID da peça.
        return sessoesDB.stream()
                .filter(sessao -> sessao.getPeca().getId().equals(idPeca))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Sessao> buscarPorId(String idSessao) {
        if (idSessao == null || idSessao.trim().isEmpty()) {
            return Optional.empty();
        }

        // Busca na lista em memória a sessão com o ID correspondente.
        return sessoesDB.stream()
                .filter(sessao -> sessao.getId().equals(idSessao))
                .findFirst();
    }
}