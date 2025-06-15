// Arquivo: aplicacao/servicos/ReservaServico.java
package com.teatroabc.aplicacao.servicos;

import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.infraestrutura.utilitarios_comuns.GeradorIdUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de aplicação para criar e consultar reservas/bilhetes.
 * Orquestra a lógica de negócio, validando regras, calculando valores e interagindo
 * com os repositórios através de suas interfaces (Portas de Saída).
 */
public class ReservaServico implements IReservaServico {

    private final IBilheteRepositorio bilheteRepositorio;
    private final IAssentoRepositorio assentoRepositorio;

    /**
     * Construtor para ReservaServico.
     * @param bilheteRepositorio Implementação da interface para persistência de bilhetes.
     * @param assentoRepositorio Implementação da interface para consulta de estado de assentos.
     * @throws IllegalArgumentException se algum dos repositórios for nulo.
     */
    public ReservaServico(IBilheteRepositorio bilheteRepositorio, IAssentoRepositorio assentoRepositorio) {
        if (bilheteRepositorio == null) {
            throw new IllegalArgumentException("Repositório de bilhetes não pode ser nulo.");
        }
        if (assentoRepositorio == null) {
            throw new IllegalArgumentException("Repositório de assentos não pode ser nulo.");
        }
        this.bilheteRepositorio = bilheteRepositorio;
        this.assentoRepositorio = assentoRepositorio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bilhete criarReserva(Sessao sessao, Cliente cliente, List<Assento> assentosSelecionados)
            throws ReservaInvalidaException, IllegalArgumentException {

        // 1. Validações de parâmetros de entrada essenciais
        if (sessao == null) throw new IllegalArgumentException("Sessão não pode ser nula para criar reserva.");
        if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo para criar reserva.");
        if (assentosSelecionados == null || assentosSelecionados.isEmpty()) {
            throw new IllegalArgumentException("A lista de assentos selecionados não pode ser vazia.");
        }

        // 2. Verificar disponibilidade dos assentos (regra de negócio)
        List<String> codigosAssentosSelecionados = assentosSelecionados.stream()
                .map(Assento::getCodigo)
                .collect(Collectors.toList());
        
        // REFATORADO: A verificação de disponibilidade agora usa o objeto Sessao.
        if (!assentoRepositorio.verificarDisponibilidade(sessao, codigosAssentosSelecionados)) {
            throw new ReservaInvalidaException("Um ou mais assentos selecionados não estão mais disponíveis para esta sessão. Por favor, tente novamente.");
        }

        // 3. Calcular subtotal dos assentos selecionados
        BigDecimal subtotal = assentosSelecionados.stream()
            .map(Assento::getPreco)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

        // 4. Obter o fator de desconto do plano de fidelidade do cliente
        BigDecimal fatorDesconto = cliente.getPlanoFidelidade().getFatorDesconto();
        
        // 5. Calcular o valor do desconto e o valor total final
        BigDecimal valorDescontoCalculado = subtotal.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorTotalFinal = subtotal.subtract(valorDescontoCalculado).setScale(2, RoundingMode.HALF_UP);

        // 6. Gerar identificadores para o novo Bilhete
        String novoIdBilhete = GeradorIdUtil.gerarNovoId();
        String novoCodigoBarras = GeradorIdUtil.gerarNovoCodigoBarras();

        // 7. Obter data e hora da compra
        LocalDateTime dataHoraCompra = LocalDateTime.now();

        // 8. Criar a instância da entidade Bilhete com o construtor que aceita Sessao
        Bilhete bilhete = new Bilhete(
            novoIdBilhete,
            novoCodigoBarras,
            sessao, // Passa o objeto Sessao inteiro
            cliente,
            assentosSelecionados,
            subtotal,
            valorDescontoCalculado,
            valorTotalFinal,
            dataHoraCompra
        );

        // 9. Persistir o bilhete. A implementação do repositório irá lidar com os detalhes.
        try {
            bilheteRepositorio.salvar(bilhete);
        } catch (Exception e) {
            throw new ReservaInvalidaException("Falha crítica ao tentar salvar o bilhete: " + e.getMessage(), e);
        }
        
        return bilhete;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bilhete> buscarBilhetesCliente(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String cpfNormalizado = cpf.replaceAll("[^0-9]", "");
        return bilheteRepositorio.listarPorCpfCliente(cpfNormalizado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Bilhete> buscarBilhetePorId(String idBilhete) {
        if (idBilhete == null || idBilhete.trim().isEmpty()) {
            return Optional.empty();
        }
        return bilheteRepositorio.buscarPorId(idBilhete);
    }
}
