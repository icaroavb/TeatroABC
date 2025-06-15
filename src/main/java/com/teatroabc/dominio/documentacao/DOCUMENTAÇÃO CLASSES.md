**Documentação para Assento.java (Formato Markdown):**

### Assento \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe Assento é responsável por encapsular os dados e o estado fundamental de um assento no teatro, como sua identificação, localização, categoria, preço fixo e status de disponibilidade. Ela não se envolve com lógicas de reserva complexas, persistência ou representação na interface do usuário.  
* **OCP (Princípio Aberto/Fechado):**  
  A entidade é projetada para ser fechada para modificações em seus atributos imutáveis e lógica central. A única alteração de estado permitida é o status. Novas funcionalidades ou tipos de assentos poderiam ser introduzidos por extensão (herança ou composição) sem alterar esta classe base.  
* **LSP (Princípio da Substituição de Liskov):**  
  Atualmente, não há subclasses de Assento, portanto este princípio não é diretamente testado em termos de substituição de tipos. Contudo, o design simples e coeso da classe não apresenta impedimentos para futuras extensões que respeitem o LSP.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A classe Assento não implementa interfaces. Seus métodos públicos (getCodigo, getPreco, setStatus, etc.) formam um conjunto coeso e específico para a manipulação de um assento, não forçando clientes a dependerem de métodos que não utilizam.  
* **DIP (Princípio da Inversão de Dependência):**  
  A classe Assento depende de abstrações estáveis como os enums CategoriaAssento e StatusAssento (que definem parte do vocabulário do domínio) e do tipo java.math.BigDecimal (um tipo base do JDK). Ela não depende de módulos concretos de baixo nível ou voláteis.

### Assento \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.enums.CategoriaAssento:  
  Importada para definir e acessar a categoria à qual o assento pertence (ex: Frisas, Balcão), que também informa o preço base para a instanciação do assento.  
* com.teatroabc.enums.StatusAssento:  
  Importada para gerenciar e consultar o estado de disponibilidade do assento (ex: Disponível, Ocupado, Selecionado).  
* java.math.BigDecimal:  
  Importada para armazenar e manipular o atributo preco do assento, garantindo precisão em cálculos financeiros e evitando problemas de arredondamento comuns com tipos de ponto flutuante primitivos.  
* java.util.Objects:  
  Importada para auxiliar na implementação dos métodos equals (para comparação segura de objetos, especialmente o codigo) e hashCode (para geração de código hash consistente).

**Documentação para Peca.java (Formato Markdown):**

### Peca \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe Peca foca em representar as informações essenciais de uma peça de teatro, como seu identificador, título, descrição, data, e atributos visuais (caminho da imagem, cor de fundo em formato hexadecimal). Ela não se envolve com lógica de venda, exibição na UI ou geração de seus próprios identificadores.  
* **OCP (Princípio Aberto/Fechado):**  
  Como uma entidade de dados imutável (todos os campos são final), é inerentemente fechada para modificação após a criação. Extensões para diferentes tipos de "eventos" ou "peças" com atributos adicionais poderiam ser feitas através de composição ou novas entidades, sem alterar a estrutura fundamental de Peca.  
* **LSP (Princípio da Substituição de Liskov):**  
  Não aplicável diretamente no momento, pois não há uma hierarquia de herança envolvendo a classe Peca.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A classe não implementa interfaces. Seus métodos públicos (getters) são coesos e fornecem acesso aos dados da peça, relevantes para qualquer consumidor da entidade.  
* **DIP (Princípio da Inversão de Dependência):**  
  A classe Peca agora depende apenas de tipos do JDK (String, LocalDateTime, Objects). A dependência anterior de java.awt.Color foi removida (substituída por String corFundoHex) e a geração de ID foi externalizada, fortalecendo o DIP e o alinhamento com a arquitetura hexagonal.

### Peca \- Bibliotecas Necessárias/Dependentes

* java.time.LocalDateTime:  
  Utilizada para armazenar e representar a data e hora exata da apresentação da peça, permitindo manipulações de data/hora padronizadas e robustas.  
* java.util.Objects:  
  Usada para fornecer implementações seguras e padronizadas para os métodos equals (comparação baseada no id) e hashCode (geração de hash a partir do id), e também para tratar subtitulo e descricao nulos no construtor com requireNonNullElse.

**Documentação para Cliente.java (Formato Markdown):**

### Cliente \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe Cliente é responsável por manter os dados cadastrais de um cliente e seu plano de fidelidade associado. Ela delega a lógica específica do plano (como cálculo de descontos) para a estratégia PlanoFidelidade injetada, mantendo sua própria responsabilidade coesa.  
* **OCP (Princípio Aberto/Fechado):**  
  A classe é fechada para modificação em relação à adição de novos planos de fidelidade; isso é feito criando novas implementações da interface PlanoFidelidade. A classe Cliente interage com a abstração PlanoFidelidade, permitindo que novas estratégias sejam adicionadas sem alterar Cliente.java.  
* **LSP (Princípio da Substituição de Liskov):**  
  Relevante para as implementações da interface PlanoFidelidade. Qualquer objeto PlanoFidelidade concreto deve ser substituível pela interface base sem alterar o comportamento esperado pela classe Cliente ao invocar métodos como calcularDesconto.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A classe Cliente interage com a interface PlanoFidelidade, que é específica e coesa para as funcionalidades de um plano. A própria classe Cliente não implementa interfaces grandes ou irrelevantes.  
* **DIP (Princípio da Inversão de Dependência):**  
  A classe Cliente depende da abstração PlanoFidelidade e não de suas implementações concretas diretamente (exceto pela definição de um plano padrão SemFidelidade no construtor como fallback, o que é aceitável). A PlanoFidelidadeFactory auxilia na inversão da criação das estratégias concretas.

### Cliente \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.dominio.fidelidade.PlanoFidelidade:  
  Interface que define o contrato para as diferentes estratégias de planos de fidelidade que um cliente pode ter.  
* com.teatroabc.dominio.fidelidade.PlanoFidelidadeFactory:  
  Classe fábrica utilizada por um dos construtores para criar a instância apropriada de PlanoFidelidade a partir de um identificador textual (útil para carregar da persistência).  
* com.teatroabc.dominio.fidelidade.SemFidelidade:  
  Implementação padrão de PlanoFidelidade, usada como fallback caso nenhum plano específico seja fornecido ou encontrado.  
* com.teatroabc.dominio.fidelidade.MembroABCGold:  
  Importada para o método de conveniência isMembroGold(), que verifica se o plano atual é do tipo MembroABCGold através de seu identificador.  
* java.math.BigDecimal:  
  Utilizada pelo método obterDescontoParaCompra (que delega para PlanoFidelidade), assegurando precisão em cálculos de descontos.  
* java.time.LocalDate:  
  Usada para armazenar e representar a data de nascimento do cliente de forma padronizada e robusta.  
* java.util.List (indiretamente, via PlanoFidelidade):  
  A interface PlanoFidelidade (e suas implementações) utilizam List\<Assento\> para calcular descontos.  
* java.util.Objects:  
  Utilizada para implementar equals e hashCode de forma segura (baseado no CPF) e para fornecer um plano de fidelidade padrão (SemFidelidade) caso um nulo seja passado ao construtor.

**Documentação para Bilhete.java (Formato Markdown):**

### Bilhete \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe Bilhete representa um registro imutável de uma transação de compra de ingresso. Suas responsabilidades são manter os dados da peça, cliente, assentos selecionados, turno, valores financeiros (subtotal, desconto, total) e informações de identificação (ID, código de barras) e data da compra. Ela não se envolve na lógica de cálculo desses valores nem na geração de seus identificadores, recebendo-os via construtor.  
* **OCP (Princípio Aberto/Fechado):**  
  Sendo uma entidade imutável e recebendo todos os seus dados via construtor, a classe Bilhete é fechada para modificações após sua criação. Novas informações que precisassem ser associadas a um bilhete exigiriam uma nova versão ou composição, não alterando o comportamento desta classe.  
* **LSP (Princípio da Substituição de Liskov):**  
  Não aplicável diretamente, pois não há hierarquia de herança para a classe Bilhete.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A classe não implementa interfaces. Seus métodos públicos (getters) são coesos e fornecem acesso aos dados intrínsecos de um bilhete.  
* **DIP (Princípio da Inversão de Dependência):**  
  A classe Bilhete depende de outras entidades de domínio (Peca, Cliente, Assento), do enum Turno e de tipos do JDK (BigDecimal, LocalDateTime, List, Objects). Todas essas são abstrações estáveis ou tipos base, alinhando-se com o DIP, pois não depende de módulos concretos voláteis de baixo nível.

### Bilhete \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.enums.Turno:  
  Importada para definir e armazenar o turno (manhã, tarde, noite) específico da sessão da peça para a qual o bilhete é válido.  
* java.math.BigDecimal:  
  Utilizada para armazenar e representar com precisão os valores monetários do bilhete: subtotal, valorDesconto e valorTotal.  
* java.math.RoundingMode:  
  Utilizada no construtor ao definir a escala dos BigDecimal para garantir o arredondamento correto (duas casas decimais para valores monetários).  
* java.time.LocalDateTime:  
  Usada para registrar e armazenar o momento exato em que o bilhete foi gerado/comprado.  
* java.util.ArrayList:  
  Utilizada internamente no construtor para criar uma cópia defensiva da lista de assentos.  
* java.util.Collections:  
  Usada para criar uma visão não modificável (unmodifiableList) da lista de assentos, tanto internamente quanto ao expor a lista através do getter, garantindo a imutabilidade da coleção de assentos do bilhete após sua criação.  
* java.util.List:  
  Interface utilizada para tipar a coleção de Assento associada ao bilhete.  
* java.util.Objects:  
  Utilizada para implementar equals e hashCode de forma segura, baseando-se no id único do bilhete.

### Reserva \- Princípios SOLID

* **SRP (Princípio da Responsabilidade Única):**  
  A classe Reserva tem a única responsabilidade de atuar como um Data Transfer Object (DTO), agrupando as informações essenciais (Cliente, Peca, List\<Assento\>, Turno) que são necessárias para iniciar uma operação de criação de bilhete. Ela não contém lógica de negócios.  
* **OCP (Princípio Aberto/Fechado):**  
  Sendo um DTO imutável com campos final, é fechada para modificação após a instanciação. Se novos dados fossem necessários para o processo de reserva, a estrutura do DTO seria alterada, o que é esperado para um objeto de transferência de dados.  
* **LSP (Princípio da Substituição de Liskov):**  
  Não aplicável diretamente, pois não há hierarquia de herança para a classe Reserva.  
* **ISP (Princípio da Segregação de Interfaces):**  
  A classe não implementa interfaces. Seus métodos (getters) são mínimos e coesos para acessar os dados que ela carrega.  
* **DIP (Princípio da Inversão de Dependência):**  
  A classe Reserva depende de outras entidades de domínio (Cliente, Peca, Assento) e do enum Turno. Como um DTO, é esperado que ela agregue esses tipos de domínio para transferência de dados. Essas dependências são de abstrações estáveis ou de outras entidades do mesmo nível de abstração.

### Reserva \- Bibliotecas Necessárias/Dependentes

* com.teatroabc.enums.Turno:  
  Importada para especificar o turno (manhã, tarde, noite) da sessão da peça para a qual a reserva (e o subsequente bilhete) está sendo solicitada.  
* java.util.ArrayList:  
  Utilizada internamente no construtor para criar uma cópia defensiva da lista de assentos recebida, antes de torná-la não modificável.  
* java.util.Collections:  
  Usada no construtor para criar uma visão não modificável (unmodifiableList) da lista de assentos, garantindo a imutabilidade da coleção de assentos dentro do objeto Reserva após sua criação.  
* java.util.List:  
  Interface utilizada para tipar a coleção de Assento que faz parte dos dados da reserva.

