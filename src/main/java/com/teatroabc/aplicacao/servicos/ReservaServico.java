package com.teatroabc.aplicacao.servicos;

import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;   // Porta de Saída
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;   // Porta de Saída
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.infraestrutura.utilitarios_comuns.GeradorIdUtil; // Utilitário para IDs

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections; // Para Collections.emptyList()
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de aplicação para criar e consultar reservas/bilhetes.
 * Orquestra a lógica de negócio, interage com as entidades de domínio e os
 * repositórios (através de suas interfaces).
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
    public Bilhete criarReserva(Peca peca, Cliente cliente, List<Assento> assentosSelecionados, Turno turno)
            throws ReservaInvalidaException, IllegalArgumentException {

        // 1. Validações de parâmetros de entrada essenciais
        if (peca == null) throw new IllegalArgumentException("Peça não pode ser nula para criar reserva.");
        if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo para criar reserva.");
        if (assentosSelecionados == null || assentosSelecionados.isEmpty()) {
            throw new IllegalArgumentException("A lista de assentos selecionados não pode ser vazia para criar reserva.");
        }
        if (turno == null) throw new IllegalArgumentException("O turno da apresentação deve ser especificado para criar reserva.");

        // 2. Verificar disponibilidade dos assentos (regra de negócio)
        List<String> codigosAssentosSelecionados = assentosSelecionados.stream()
                .map(Assento::getCodigo)
                .collect(Collectors.toList());
        
        if (!assentoRepositorio.verificarDisponibilidade(peca.getId(), turno, codigosAssentosSelecionados)) {
            // Melhoria: Logar quais assentos não estão disponíveis ou retornar essa informação na exceção.
            throw new ReservaInvalidaException("Um ou mais assentos selecionados não estão mais disponíveis para este turno. Por favor, selecione outros.");
        }

        // 3. Calcular subtotal dos assentos selecionados
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Assento assento : assentosSelecionados) {
            if (assento == null || assento.getPreco() == null) {
                 // Isso não deveria acontecer se a lista de assentos for válida
                 throw new ReservaInvalidaException("Encontrado assento inválido ou com preço nulo na seleção.");
            }
            subtotal = subtotal.add(assento.getPreco());
        }
        // Arredondar subtotal para 2 casas decimais
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        // 4. Obter o fator de desconto do plano de fidelidade do cliente
        BigDecimal fatorDesconto = cliente.getPlanoFidelidade().getFatorDesconto();
        if (fatorDesconto == null || fatorDesconto.compareTo(BigDecimal.ZERO) < 0 || fatorDesconto.compareTo(BigDecimal.ONE) > 0) {
            // Fator de desconto deve estar entre 0 e 1. Tratar como erro ou assumir 0.
            System.err.println("Aviso: Fator de desconto inválido (" + fatorDesconto + ") retornado pelo plano " +
                               cliente.getPlanoFidelidade().getNomePlano() + ". Assumindo 0% de desconto.");
            fatorDesconto = BigDecimal.ZERO;
        }
        
        // 5. Calcular o valor do desconto e o valor total final
        BigDecimal valorDescontoCalculado = subtotal.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal valorTotalFinal = subtotal.subtract(valorDescontoCalculado);
        // Garante que o total não seja negativo (embora com fator de desconto entre 0-1, não deveria ser,
        // a menos que o subtotal fosse negativo, o que não deve ocorrer).
        if (valorTotalFinal.compareTo(BigDecimal.ZERO) < 0) {
            valorTotalFinal = BigDecimal.ZERO;
        }
        // A entidade Bilhete também aplica setScale, mas é bom ter aqui para consistência.
        valorTotalFinal = valorTotalFinal.setScale(2, RoundingMode.HALF_UP);

        // 6. Gerar identificadores para o novo Bilhete
        String novoIdBilhete = GeradorIdUtil.gerarNovoId();
        String novoCodigoBarras = GeradorIdUtil.gerarNovoCodigoBarras();

        // 7. Obter data e hora da compra
        LocalDateTime dataHoraCompra = LocalDateTime.now();

        // 8. Criar a instância da entidade Bilhete
        Bilhete bilhete = new Bilhete(
            novoIdBilhete,
            novoCodigoBarras,
            peca,
            cliente,
            assentosSelecionados,
            turno,
            subtotal,                 // Subtotal calculado
            valorDescontoCalculado,   // Desconto calculado
            valorTotalFinal,          // Total final calculado
            dataHoraCompra
        );

        // 9. Persistir o bilhete
        try {
            bilheteRepositorio.salvar(bilhete, turno); // O repositório lida com a marcação de assentos
        } catch (Exception e) {
            // Em um sistema real, considerar estratégias de rollback ou compensação
            // se a marcação de assentos e o salvamento do bilhete não forem atômicos.
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
            return Collections.emptyList(); // Retorna lista vazia imutável
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