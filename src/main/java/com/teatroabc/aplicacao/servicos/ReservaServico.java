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
import com.teatroabc.infraestrutura.utilitarios_comuns.GeradorIdUtil;                   // Utilitário para IDs

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
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
    // private final IValidadorDisponibilidadeAssento validadorDisponibilidade; // Exemplo de outra porta se a lógica fosse mais complexa

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

        // 1. Validações de parâmetros de entrada
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
            // Poderia obter os assentos específicos que falharam e incluí-los na mensagem
            throw new ReservaInvalidaException("Um ou mais assentos selecionados não estão mais disponíveis para este turno. Por favor, selecione outros.");
        }

        // 3. Calcular valores financeiros
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Assento assento : assentosSelecionados) {
            // O preço do assento já é BigDecimal e está fixado no objeto Assento, conforme refatorado.
            if (assento.getPreco() == null) { // Checagem extra de sanidade
                throw new ReservaInvalidaException("Assento " + assento.getCodigo() + " com preço nulo encontrado.");
            }
            subtotal = subtotal.add(assento.getPreco());
        }

        BigDecimal valorDesconto = cliente.obterDescontoParaCompra(assentosSelecionados); // Cliente delega para seu PlanoFidelidade

        BigDecimal valorTotalFinal = subtotal.subtract(valorDesconto);
        // Garante que o total não seja negativo e arredonda
        if (valorTotalFinal.compareTo(BigDecimal.ZERO) < 0) {
            valorTotalFinal = BigDecimal.ZERO;
        }
        valorTotalFinal = valorTotalFinal.setScale(2, RoundingMode.HALF_UP);

        // Subtotal e desconto também devem ter a escala correta se forem persistidos com precisão.
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        valorDesconto = valorDesconto.setScale(2, RoundingMode.HALF_UP);


        // 4. Gerar identificadores para o novo Bilhete
        String novoIdBilhete = GeradorIdUtil.gerarNovoId();
        String novoCodigoBarras = GeradorIdUtil.gerarNovoCodigoBarras(); // Utilizar um método específico se a lógica for diferente

        // 5. Obter data e hora da compra
        LocalDateTime dataHoraCompra = LocalDateTime.now();

        // 6. Criar a instância da entidade Bilhete (agora um portador de dados imutável)
        Bilhete bilhete = new Bilhete(
                novoIdBilhete,
                novoCodigoBarras,
                peca,
                cliente,
                assentosSelecionados, // A lista de objetos Assento
                turno,
                subtotal,
                valorDesconto,
                valorTotalFinal,
                dataHoraCompra
        );

        // 7. Persistir o bilhete (o repositório lida com a persistência e com a marcação dos assentos como ocupados)
        try {
            bilheteRepositorio.salvar(bilhete, turno); // Passa o turno para o repositório também
        } catch (Exception e) {
            // Logar o erro original
            // Em um sistema real, poderia haver lógica para tentar reverter a marcação de assentos se o salvamento falhar,
            // ou usar um padrão de unidade de trabalho/transação.
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
        String cpfNormalizado = cpf.replaceAll("[^0-9]", ""); // Normalização aqui
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