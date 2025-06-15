### IClienteServico \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  

  A interface IClienteServico define um contrato coeso para as operações de negócio relacionadas à entidade Cliente. Cada método representa uma capacidade distinta do serviço (cadastrar, buscar, verificar existência).

* **OCP (Princípio Aberto/Fechado):**  

  A interface é aberta para implementação por diferentes classes de serviço (embora tenhamos apenas uma, ClienteServico). Se novas operações de cliente fossem necessárias, a interface poderia ser estendida (idealmente de forma não disruptiva) ou novas interfaces mais específicas poderiam ser criadas.  

* **LSP (Princípio da Substituição de Liskov):**  

  Qualquer implementação de IClienteServico deve aderir ao contrato definido, garantindo que possa ser substituída sem quebrar a funcionalidade esperada pelos clientes da interface.  

* **ISP (Princípio da Segregação de Interfaces):**  

  A interface é específica para os serviços de cliente. Se houvesse um número muito grande e variado de operações, poderia ser dividida, mas atualmente seus métodos são relacionados e formam um contrato lógico.  

* **DIP (Princípio da Inversão de Dependência):**  

  Esta interface é uma abstração (uma "Porta de Entrada" na arquitetura hexagonal) da qual as camadas externas (como a UI ou controladores) dependerão, em vez de dependerem da implementação concreta ClienteServico.

### IClienteServico \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.dominio.modelos.Cliente:  
  Define o tipo de retorno para operações de busca e cadastro, representando a entidade de domínio Cliente.  

* com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO:  
  Especifica o Data Transfer Object usado como parâmetro para o método cadastrar, encapsulando os dados necessários para criar um novo cliente.  

* com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException:  
  Define uma exceção específica que pode ser lançada pelo método cadastrar para indicar que um cliente com o CPF fornecido já existe.  
  
* java.util.Optional:  
  Utilizada como tipo de retorno para buscarPorCpf, indicando de forma clara que um cliente pode ou não ser encontrado, evitando a necessidade de retornos nulos.

### ClienteServico \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe ClienteServico é responsável por orquestrar as operações de negócio relacionadas aos clientes, como cadastro e consulta. Ela lida com validações de entrada (formato de data, existência de CPF), normalização de dados (CPF) e coordena a interação com o IClienteRepositorio para persistência, mantendo uma responsabilidade coesa.  
* **OCP (Princípio Aberto/Fechado):**  
  A classe depende da interface IClienteRepositorio, permitindo que diferentes implementações de persistência sejam usadas sem modificar o serviço. A lógica de cadastro é extensível em termos de planos de fidelidade através da PlanoFidelidadeFactory, sem requerer alterações diretas aqui para novos planos.  
* **LSP (Princípio da Substituição de Liskov):**  
  A classe utiliza a interface IClienteRepositorio. Qualquer implementação concreta desta interface deve ser substituível e comportar-se conforme o contrato, garantindo que o ClienteServico funcione corretamente.  
* **ISP (Princípio da Segregação de Interfaces):**  
  ClienteServico implementa IClienteServico, uma interface específica para as operações de cliente. Ela também depende de IClienteRepositorio, que é focada nas operações de persistência de cliente. Ambas as interfaces são coesas.  
* **DIP (Princípio da Inversão de Dependência):**  
  A classe ClienteServico depende da abstração IClienteRepositorio (Porta de Saída), que é injetada via construtor. Isso desacopla o serviço da implementação concreta da camada de persistência, permitindo flexibilidade e testabilidade.

### ClienteServico \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.dominio.modelos.Cliente:  
  Utilizada como o tipo de objeto retornado e manipulado internamente, representando a entidade de domínio Cliente.  
* com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio:  
  Interface que define o contrato para operações de persistência de clientes. ClienteServico depende desta abstração para salvar e buscar clientes.  
* com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO:  
  DTO utilizado para receber os dados de entrada para o cadastro de um novo cliente de forma estruturada.  
* com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException:  
  Exceção customizada lançada pelo serviço para indicar que uma tentativa de cadastro foi feita para um CPF que já existe.  
* com.teatroabc.aplicacao.interfaces.IClienteServico:  
  Interface que esta classe implementa, definindo o contrato público do serviço de cliente.  
* java.util.Optional:  
  Utilizada para o tipo de retorno do método buscarPorCpf, indicando que um cliente pode ou não ser encontrado.  
* java.time.LocalDate:  
  Usada para representar e manipular a data de nascimento do cliente.  
* java.time.format.DateTimeFormatter:  
  Utilizada para parsear a string da data de nascimento (vinda do DTO) para um objeto LocalDate.  
* java.time.format.DateTimeParseException:  
  Exceção que pode ser lançada durante o parse da data de nascimento, tratada no serviço.

### IPecaServicos \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A interface IPecaServico define um contrato estritamente focado nas operações de consulta da entidade Peca. As responsabilidades de gerenciamento de assentos foram removidas, melhorando a coesão.  
* **OCP (Princípio Aberto/Fechado):**  
  A interface é aberta para ser implementada por diferentes serviços de peça, se necessário, e fechada para modificações que quebrem seus clientes, uma vez que seu contrato está definido.  
* **LSP (Princípio da Substituição de Liskov):**  
  Qualquer classe que implemente IPecaServico deve aderir ao comportamento esperado pelos métodos definidos, garantindo a substituibilidade.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A interface agora é mais enxuta e específica para as operações de Peca, evitando que clientes dependam de métodos não relacionados (como os de assentos).  
* **DIP (Princípio da Inversão de Dependência):**  
  Como uma abstração (Porta de Entrada), permite que camadas externas dependam dela em vez de implementações concretas, promovendo o desacoplamento.

### IPecaServicos \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.dominio.modelos.Peca:  
  Define o tipo de objeto que é retornado pelos métodos de busca, representando a entidade de domínio Peca.  
* java.util.List:  
  Interface utilizada como tipo de retorno para buscarTodasPecas, representando uma coleção de objetos Peca.  
* java.util.Optional:  
  Utilizada como tipo de retorno para buscarPecaPorId, para indicar de forma explícita que uma peça pode ou não ser encontrada para o ID fornecido.

### PecaServicos \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe PecaServico é responsável por fornecer funcionalidades de consulta relacionadas à entidade Peca. Ela delega a responsabilidade de acesso aos dados para a interface IPecaRepositorio, focando apenas na orquestração da busca de peças.  
* **OCP (Princípio Aberto/Fechado):**  
  A classe depende da interface IPecaRepositorio, permitindo que a implementação da persistência de peças seja alterada sem a necessidade de modificar o PecaServico.  
* **LSP (Princípio da Substituição de Liskov):**  
  A classe utiliza a interface IPecaRepositorio. Qualquer implementação concreta desta interface deve ser substituível, aderindo ao contrato esperado para que o PecaServico funcione corretamente.  
* **ISP (Princípio da Segregação de Interfaces):**  
  PecaServico implementa IPecaServico, que é uma interface enxuta e específica para as operações de consulta de peças. A dependência IPecaRepositorio também é específica.  
* **DIP (Princípio da Inversão de Dependência):**  
  A classe depende da abstração IPecaRepositorio (Porta de Saída), injetada via construtor. Isso remove o acoplamento direto a uma implementação concreta de repositório, crucial para a arquitetura hexagonal e testabilidade.

### PecaServicos \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.dominio.modelos.Peca:  
  Utilizada como o tipo de objeto retornado nas operações de busca, representando a entidade de domínio Peca.  
* com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio:  
  Interface que define o contrato para as operações de acesso aos dados de peças. PecaServico depende desta abstração para buscar as informações das peças.  
* com.teatroabc.aplicacao.interfaces.IPecaServico:  
  Interface que esta classe implementa, definindo o contrato público do serviço de peças.  
* java.util.Collections:  
  Utilizada no método buscarTodasPecas para fornecer uma lista vazia imutável como fallback seguro caso o repositório retorne nulo.  
* java.util.List:  
  Interface utilizada para tipar a coleção de Peca retornada por buscarTodasPecas.  
* java.util.Optional:  
  Utilizada como tipo de retorno para buscarPecaPorId, indicando que uma peça pode ou não ser encontrada.

### IReservaServicos \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A interface define um contrato para o serviço de reservas, que engloba a criação de novas reservas/bilhetes e a consulta de bilhetes existentes. Estas são responsabilidades coesas dentro do contexto de "gerenciamento de reservas/bilhetes".  
* **OCP (Princípio Aberto/Fechado):**  
  Como interface, é aberta para diferentes implementações. Se novas formas de criar reservas ou consultar bilhetes surgissem, idealmente seriam novas implementações ou extensões da interface (embora modificar interfaces seja geralmente evitado em favor de novas interfaces).  
* **LSP (Princípio da Substituição de Liskov):**  
  Qualquer implementação de IReservaServico deve cumprir o contrato, garantindo que os clientes da interface possam usar qualquer implementação de forma intercambiável.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A interface é focada nas operações de reserva e bilhete. Se as responsabilidades se tornassem muito amplas (ex: gerenciamento de pagamentos complexos, notificações), poderia ser dividida. No estado atual, é coesa.  
* **DIP (Princípio da Inversão de Dependência):**  
  Esta interface é a abstração (Porta de Entrada) da qual as camadas externas (UI, controladores) dependerão, em vez da implementação concreta ReservaServico.

### IReservaServicos \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.dominio.modelos.Bilhete:  
  Define o tipo de objeto retornado pela operação de criação de reserva e pelas operações de busca.  
* com.teatroabc.dominio.modelos.Cliente:  
  Usado como parâmetro na criação da reserva, representa o cliente associado ao bilhete.  
* com.teatroabc.dominio.modelos.Peca:  
  Usado como parâmetro na criação da reserva, representa a peça para a qual o bilhete é gerado.  
* com.teatroabc.dominio.modelos.Assento:  
  Usado como parâmetro na criação da reserva (uma lista de assentos selecionados).  
* com.teatroabc.enums.Turno:  
  Usado como parâmetro na criação da reserva para especificar o turno da apresentação.  
* com.teatroabc.aplicacao.excecoes.ReservaInvalidaException:  
  Define uma exceção de negócio específica que pode ser lançada pelo método criarReserva.  
* java.util.List:  
  Interface utilizada para tipar a coleção de Assento (parâmetro) e Bilhete (retorno).  
* java.util.Optional:  
  Utilizada como tipo de retorno para buscarBilhetePorId, para indicar que um bilhete pode ou não ser encontrado.

### ReservaServicos \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe ReservaServico é responsável por orquestrar o caso de uso de "criar uma reserva" (resultando em um bilhete) e consultar bilhetes. Ela coordena a validação de disponibilidade, cálculo de preços (delegando ao cliente para descontos), geração de identificadores e a interação com os repositórios para persistência.  
* **OCP (Princípio Aberto/Fechado):**  
  A classe depende de interfaces para os repositórios (IBilheteRepositorio, IAssentoRepositorio), permitindo que as implementações de persistência ou consulta de assentos sejam trocadas sem modificar o serviço. A lógica de cálculo de desconto é extensível através do padrão Strategy no Cliente.  
* **LSP (Princípio da Substituição de Liskov):**  
  ReservaServico utiliza as interfaces IBilheteRepositorio e IAssentoRepositorio. Qualquer implementação concreta dessas interfaces deve ser substituível e comportar-se conforme o contrato definido.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A classe implementa IReservaServico, que é específica para as operações de reserva/bilhete. As interfaces de repositório que ela consome também são específicas para suas respectivas entidades.  
* **DIP (Princípio da Inversão de Dependência):**  
  ReservaServico depende das abstrações IBilheteRepositorio e IAssentoRepositorio (Portas de Saída), que são injetadas via construtor. Isso remove o acoplamento a implementações concretas, fundamental para a arquitetura hexagonal.

### Reserva Servicos \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.dominio.modelos.Bilhete:  
  Representa a entidade de domínio principal que este serviço cria e retorna.  
* com.teatroabc.dominio.modelos.Cliente:  
  Entidade de domínio que representa o cliente, usada para obter informações e aplicar descontos de fidelidade.  
* com.teatroabc.dominio.modelos.Peca:  
  Entidade de domínio que representa a peça selecionada para a reserva.  
* com.teatroabc.dominio.modelos.Assento:  
  Entidade de domínio que representa os assentos selecionados, usada para cálculo de subtotal e verificação de disponibilidade.  
* com.teatroabc.enums.Turno:  
  Enumeração usada para especificar e registrar o turno da apresentação do bilhete.  
* com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio:  
  Interface (Porta de Saída) para operações de persistência relacionadas a Bilhete.  
* com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio:  
  Interface (Porta de Saída) para operações de consulta do estado e disponibilidade de Assento.  
* com.teatroabc.aplicacao.excecoes.ReservaInvalidaException:  
  Exceção de negócio customizada lançada quando uma reserva não pode ser concluída.  
* com.teatroabc.aplicacao.interfaces.IReservaServico:  
  Interface que esta classe implementa, definindo o contrato público do serviço.  
* com.teatroabc.infraestrutura.utilitarios_comuns.GeradorIdUtil:  
  Classe utilitária usada para gerar IDs únicos para bilhetes e códigos de barras.  
* java.math.BigDecimal:  
  Utilizada para todos os cálculos e representações de valores financeiros (subtotal, desconto, total) para garantir precisão.  
* java.math.RoundingMode:  
  Usada para definir o modo de arredondamento ao escalar os valores BigDecimal.  
* java.time.LocalDateTime:  
  Usada para registrar a data e hora exatas da criação/compra do bilhete.  
* java.util.List:  
  Interface utilizada para coleções de Assento e Bilhete.  
* java.util.Optional:  
  Utilizada para o tipo de retorno de buscarBilhetePorId.  
* java.util.stream.Collectors:  
  Utilizada para coletar os códigos dos assentos selecionados em uma lista de Strings para verificação de disponibilidade.

**ocumentação para dto/DadosCadastroClienteDTO.java (Formato Markdown):**

### DTO \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe DadosCadastroClienteDTO tem a única e clara responsabilidade de atuar como um contêiner de dados para transferir as informações necessárias ao cadastro de um novo cliente. Ela não contém lógica de negócios, validações complexas (além de possíveis checagens de nulidade no construtor) ou interações com outras partes do sistema.  
* **OCP (Princípio Aberto/Fechado):**  
  Sendo um objeto de dados imutável e simples, é fechado para modificação após a instanciação. Se novos campos fossem necessários para o cadastro, a estrutura do DTO seria alterada, o que é uma mudança esperada para um objeto de transferência. Não há comportamento complexo que necessite ser aberto para extensão.  
* **LSP (Princípio da Substituição de Liskov):**  
  Não aplicável diretamente, pois DTOs raramente participam de hierarquias de herança onde a substituibilidade é uma preocupação primária.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A classe não implementa interfaces. Ela expõe apenas os getters necessários para acessar os dados que carrega, sendo coesa e específica para seu propósito.  
* **DIP (Princípio da Inversão de Dependência):**  
  Como um DTO, esta classe geralmente não depende de abstrações de baixo nível; ela é composta por tipos de dados básicos (Strings). Camadas de serviço dependem dela como um parâmetro, mas ela em si tem poucas ou nenhuma dependência externa complexa.

### DTO \- Bibliotecas Necessárias/Dependentes

* Nenhuma biblioteca externa específica além dos tipos padrão do Java (String) é diretamente requerida por esta classe DTO. Ela é autocontida.

### ClienteJaCadastrado \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe ClienteJaCadastradoException tem a única responsabilidade de representar uma condição de erro específica: a tentativa de cadastrar um cliente que já existe. Ela encapsula essa semântica de erro.  
* **OCP (Princípio Aberto/Fechado):**  
  Como uma classe de exceção, ela é fechada para modificação em seu propósito fundamental. Se novos tipos de erros de cadastro de cliente fossem necessários, seriam criadas novas classes de exceção, possivelmente herdando de uma exceção base mais genérica se apropriado, mas não alterando esta.  
* **LSP (Princípio da Substituição de Liskov):**  
  Sendo uma Exception, ela pode ser usada em qualquer lugar onde uma Exception (ou Throwable) é esperada, e os blocos catch podem tratá-la especificamente ou de forma mais genérica.  
* **ISP (Princípio da Segregação de Interfaces):**  
  Não aplicável diretamente, pois não implementa interfaces de negócio.  
* **DIP (Princípio da Inversão de Dependência):**  
  Não aplicável diretamente no contexto de uma classe de exceção simples que herda de java.lang.Exception.

### ClienteJaCadastrado \- Bibliotecas Necessárias/Dependentes

* Nenhuma biblioteca externa específica além das classes base do Java (java.lang.Exception, java.lang.String, java.lang.Throwable) é diretamente requerida por esta classe de exceção.

### Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe ReservaInvalidaException tem a responsabilidade singular de representar um erro de negócio específico ocorrido durante o processamento de uma reserva (que leva à criação de um bilhete), como a indisponibilidade de assentos ou outras regras de negócio não satisfeitas.  
* **OCP (Princípio Aberto/Fechado):**  
  Como classe de exceção, seu propósito fundamental é fixo. Se diferentes tipos de invalidez de reserva precisassem de tratamento distinto, novas classes de exceção poderiam ser criadas (possivelmente herdando desta ou de uma exceção de negócio mais genérica).  
* **LSP (Princípio da Substituição de Liskov):**  
  Sendo uma Exception, pode ser capturada e tratada por blocos catch que esperam ReservaInvalidaException, Exception, ou Throwable, mantendo o comportamento esperado de uma exceção.  
* **ISP (Princípio da Segregação de Interfaces):**  
  Não aplicável diretamente, pois não implementa interfaces de negócio.  
* **DIP (Princípio da Inversão de Dependência):**  
  Não aplicável diretamente no contexto de uma classe de exceção simples que herda de java.lang.Exception.

### Bibliotecas Necessárias/Dependentes

* Nenhuma biblioteca externa específica além das classes base do Java (java.lang.Exception, java.lang.String, java.lang.Throwable) é diretamente requerida por esta classe de exceção.

