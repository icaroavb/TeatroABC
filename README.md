**O objetivo principal é que as classes da UI (suas Telas e Componentes Swing):**

- Não contenham lógica de negócio.
- Não acessem diretamente os repositórios ou o GerenciadorArquivos.
- Interajam com o núcleo da aplicação exclusivamente através das interfaces de serviço (Portas de Entrada como IClienteServico, IPecaServico, IReservaServico).
- Recebam as instâncias dos serviços via Injeção de Dependência.
- Construam DTOs para enviar dados aos serviços.
- Tratem exceções (de negócio e de validação) retornadas pelos serviços e apresentem feedback apropriado ao usuário.
- Sejam responsáveis pela apresentação dos dados obtidos do domínio (ex: converter corFundoHex para java.awt.Color, usar FormatadorData, FormatadorMoeda).

**O cerne da Arquitetura Hexagonal é proteger o Domínio de Negócio (o "hexágono") de influências e dependências de tecnologias externas (como UI, bancos de dados, frameworks específicos, etc.).**

*O Domínio no Centro:*

  Toda a arquitetura é construída ao redor do domínio. O domínio contém a lógica de negócios pura, as entidades, os objetos de valor, os eventos de domínio e os serviços de domínio.
  Ele é o coração da aplicação e deve ser independente de como os dados são apresentados ao usuário (UI) ou como são persistidos (banco de dados).
  
*Portas como Contratos do Domínio:*

  As Portas são interfaces definidas pelo domínio ou para o domínio.
  Portas de Entrada (Driving/Primary Ports): Definem como o mundo exterior pode interagir com a lógica de aplicação do domínio (Casos de Uso/Serviços de Aplicação). Elas são a API do seu domínio.
  Portas de Saída (Driven/Secondary Ports): Definem os contratos que o domínio precisa para interagir com o mundo exterior (ex: "preciso salvar esses dados", "preciso buscar esses dados"). O domínio não se importa como isso é feito, apenas que existe uma implementação que cumpre o contrato.

Adaptadores como Implementações Tecnológicas:

  Os Adaptadores ficam fora do hexágono e implementam as portas.
  Adaptadores Primários (Driving/Primary Adapters): São os iniciadores da ação. Ex: Controladores de uma API REST, componentes de uma UI Swing, testes de unidade que chamam os serviços de aplicação. Eles traduzem a entrada do usuário/sistema em chamadas para as Portas de Entrada.
  Adaptadores Secundários (Driven/Secondary Adapters): São implementações das Portas de Saída. Ex: Um repositório que usa JDBC para falar com um banco SQL, um cliente HTTP para consumir uma API externa, um adaptador que envia e-mails. Eles traduzem as necessidades do domínio (definidas pelas Portas de Saída) em interações com tecnologias específicas.
  
Orientada a Domínio vs. Orientada a Classes:

  Orientada a Domínio: O foco principal é modelar o problema de negócio de forma rica e expressiva, com a lógica de negócio residindo nesse modelo. As decisões de design são guiadas pelas necessidades e pelo vocabulário do domínio. A tecnologia é uma preocupação secundária e "conectada" ao domínio através de adaptadores.
  Orientada a Classes (em um sentido mais genérico): Todas as abordagens orientadas a objetos usam classes. No entanto, uma arquitetura pode ser "orientada a classes" sem ser "orientada a domínio" se, por exemplo, as classes estiverem fortemente acopladas à tecnologia de UI ou persistência, ou se a lógica de negócio estiver espalhada e não centralizada em um modelo de domínio coeso. Um exemplo seria um "Transaction Script" onde a lógica reside em classes de serviço que operam sobre estruturas de dados simples, com pouco comportamento nas "entidades".
  Por que a Arquitetura Hexagonal é orientada a domínio:
  
Isolamento do Domínio: Seu principal objetivo é permitir que o domínio evolua independentemente das tecnologias de entrega ou infraestrutura.

Testabilidade do Domínio: O domínio pode ser testado em isolamento, sem a necessidade de UI, banco de dados, etc., pois suas dependências externas são abstraídas por portas (que podem ser mockadas/stubadas).
Linguagem Ubíqua (DDD): Encoraja o uso de uma linguagem comum entre desenvolvedores e especialistas do domínio, refletida nas entidades e serviços do domínio.
Tecnologia como Detalhe: A escolha de um banco de dados específico, um framework de UI, ou um serviço de mensageria torna-se um detalhe de implementação que pode ser trocado com menor impacto no núcleo da aplicação.

(atualização conforme a evolução do projeto)

		com.teatroabc
		├── aplicacao                   // Camada de Aplicação (Casos de Uso, Portas de Entrada)
		│   ├── dto                     // Data Transfer Objects usados pelos serviços de aplicação
		│   │   └── DadosCadastroClienteDTO.java
		│   ├── excecoes                // Exceções específicas da camada de aplicação/serviço
		│   │   ├── ClienteJaCadastradoException.java
		│   │   └── ReservaInvalidaException.java
		│   ├── interfaces              // Interfaces dos Serviços de Aplicação (Portas de Entrada)
		│   │   ├── IClienteServico.java
		│   │   ├── IPecaServico.java
		│   │   └── IReservaServico.java
		│   └── servicos                // Implementações dos Serviços de Aplicação
		│       ├── ClienteServico.java
		│       ├── PecaServico.java
		│       └── ReservaServico.java
		│
		├── dominio                     // Núcleo do Domínio (O Hexágono em si)
		│   ├── modelos                 // Entidades, Objetos de Valor, Agregados
		│   │   ├── Assento.java
		│   │   ├── Bilhete.java
		│   │   ├── Cliente.java
		│   │   ├── Peca.java
		│   │   └── Reserva.java        // (Se mantida como DTO de domínio, ou poderia ir para aplicacao.dto se for puramente de entrada)
		│   ├── enums                   // Enumerações que fazem parte do vocabulário do domínio
		│   │   ├── CategoriaAssento.java
		│   │   ├── StatusAssento.java
		│   │   ├── StatusPagamento.java
		│   │   └── Turno.java
		│   ├── fidelidade              // Subdomínio ou módulo de fidelidade (estratégias, etc.)
		│   │   ├── PlanoFidelidade.java
		│   │   ├── SemFidelidade.java
		│   │   ├── MembroABCGold.java
		│   │   └── PlanoFidelidadeFactory.java
		│   └── validadores             // Utilitários/Regras de validação de domínio puro (se houver mais)
		│       └── ValidadorCPF.java   // (Pode ficar aqui ou em utilitarios.dominio)
		│
		├── infraestrutura              // Adaptadores Secundários/Driven e Utilitários de Infraestrutura
		│   ├── persistencia            // Adaptadores para persistência de dados
		│   │   ├── interfaces          // Interfaces dos Repositórios (Portas de Saída)
		│   │   │   ├── IAssentoRepositorio.java
		│   │   │   ├── IBilheteRepositorio.java
		│   │   │   ├── IClienteRepositorio.java
		│   │   │   └── IPecaRepositorio.java
		│   │   ├── implementacao       // Implementações concretas dos Repositórios (Adaptadores Secundários)
		│   │   │   ├── AssentoRepositorio.java
		│   │   │   ├── BilheteRepositorio.java
		│   │   │   ├── ClienteRepositorio.java
		│   │   │   └── PecaRepositorio.java
		│   │   └── util                // Utilitários específicos da camada de persistência
		│   │       └── GerenciadorArquivos.java
		│   ├── ui_swing                // Adaptadores Primários/Driving para a UI Swing e utilitários de UI
		│   │   ├── componentes         // Componentes Swing customizados
		│   │   │   ├── BotaoAnimado.java
		│   │   │   ├── BotaoAssento.java
		│   │   │   ├── CardBilhete.java
		│   │   │   ├── CardPeca.java
		│   │   │   ├── LogoTeatro.java
		│   │   │   └── PainelCodigoBarras.java
		│   │   ├── telas               // Telas/Views da aplicação Swing
		│   │   │   ├── DialogoDetalhesBilhete.java
		│   │   │   ├── TelaCadastrar.java
		│   │   │   ├── TelaConfirmarPedido.java
		│   │   │   ├── TelaInformarCPF.java
		│   │   │   ├── TelaListaBilhetes.java
		│   │   │   ├── TelaPrincipal.java
		│   │   │   ├── TelaSelecionarAssento.java
		│   │   │   └── TelaSelecionarPeca.java
		│   │   ├── util                // Utilitários específicos da UI Swing
		│   │   │   ├── CarregadorImagem.java
		│   │   │   ├── FormatadorData.java      // (Se usado primariamente pela UI)
		│   │   │   ├── FormatadorMoeda.java     // (Se usado primariamente pela UI)
		│   │   │   └── SwingConfig.java         // (Ex: UIManager.setLookAndFeel)
		│   │   └── constantes_ui         // Constantes específicas da UI (se Constantes.java for dividida)
		│   │       └── CoresUI.java
		│   │       └── FontesUI.java
		│   ├── utilitarios_comuns      // Utilitários genéricos que podem ser usados por várias camadas (com cuidado)
		│   │   └── GeradorIdUtil.java    // (Ou poderia estar em infraestrutura.identificacao)
		│
		└── TeatroABCApplication.java     // Ponto de entrada da aplicação, configuração da Injeção de Dependência
