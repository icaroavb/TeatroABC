package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelFormularioCliente;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelOpcaoFidelidade;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela que orquestra o fluxo de cadastro de cliente, delegando a
 * renderização do formulário e de outras partes para componentes encapsulados.
 */
public class TelaCadastrar extends JPanel {
    private final Sessao sessao;
    private final List<Assento> assentosSelecionados;

    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

    // Referências aos novos componentes encapsulados
    private PainelFormularioCliente painelFormulario;

    public TelaCadastrar(String cpf, Sessao sessao, List<Assento> assentosSelecionados,
                         IClienteServico clienteServico, IPecaServico pecaServico, 
                         IReservaServico reservaServico, ISessaoServico sessaoServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos.");
        }
        this.sessao = sessao;
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        this.sessaoServico = sessaoServico;
        
        configurarTelaVisual(cpf);
    }
    
    private void configurarTelaVisual(String cpfInicial) {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        containerPrincipal.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(30));

        JLabel titulo = new JLabel("CADASTRAR");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createVerticalStrut(40));

        // MUDANÇA: Instancia o formulário encapsulado, passando o CPF inicial.
        this.painelFormulario = new PainelFormularioCliente(cpfInicial);
        containerPrincipal.add(this.painelFormulario);
        
        // MUDANÇA: Instancia o painel de fidelidade e define sua ação.
        // A ação do checkbox agora chama um método no painel do formulário.
        PainelOpcaoFidelidade painelFidelidade = new PainelOpcaoFidelidade(e -> {
            boolean selecionado = ((JCheckBox)e.getSource()).isSelected();
            this.painelFormulario.setCamposMembroGoldVisivel(selecionado);
        });
        containerPrincipal.add(Box.createVerticalStrut(30));
        containerPrincipal.add(painelFidelidade);

        BotaoAnimado btnCadastrarUI = new BotaoAnimado("CADASTRAR", 
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnCadastrarUI.setFont(new Font("Arial", Font.BOLD, 28));
        btnCadastrarUI.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrarUI.addActionListener(e -> realizarCadastro()); 

        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(btnCadastrarUI);
        containerPrincipal.add(Box.createVerticalStrut(30));

        scrollPane.setViewportView(containerPrincipal);
        add(scrollPane, BorderLayout.CENTER);

        // O botão de voltar permanece aqui, como parte do layout geral da tela.
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setBackground(Constantes.AZUL_ESCURO);
        JButton btnVoltar = new JButton("<< Voltar");
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> voltar());
        painelVoltar.add(btnVoltar);
        add(painelVoltar, BorderLayout.SOUTH);
    }

    /**
     * Coleta os dados do formulário, valida, e chama o serviço de cliente para registrar.
     * Navega para a tela apropriada em caso de sucesso.
     */
    private void realizarCadastro() {
        // A validação agora é delegada ao componente do formulário.
        if (!painelFormulario.validarCampos()) {
            return;
        }

        try {
            // Os dados são obtidos diretamente do componente através do método getDadosCadastroDTO.
            DadosCadastroClienteDTO dto = painelFormulario.getDadosCadastroDTO();
            
            Cliente clienteCadastrado = this.clienteServico.cadastrar(dto);
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (this.sessao != null && this.assentosSelecionados != null) {
                frame.setContentPane(new TelaConfirmarPedido(
                    clienteCadastrado, this.sessao, this.assentosSelecionados,
                    this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico
                ));
            } else {
                String mensagem = clienteCadastrado.getPlanoFidelidade().getIdentificadorPlano().equals("GOLD") ? 
                    "Cliente cadastrado como membro ABC GOLD!\n" + clienteCadastrado.getDescricaoBeneficiosPlano() :
                    "Cliente cadastrado com sucesso!";
                JOptionPane.showMessageDialog(this, mensagem, "Cadastro Realizado", JOptionPane.INFORMATION_MESSAGE);
                frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico));
            }

            frame.revalidate();
            frame.repaint();

        } catch (ClienteJaCadastradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro nos dados informados: " + e.getMessage(), "Dados Inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (this.sessao != null) {
            frame.setContentPane(new TelaInformarCPF(
                false, this.sessao, this.assentosSelecionados,
                this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico
            ));
        } else {
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico));
        }
        frame.revalidate();
        frame.repaint();
    }
}