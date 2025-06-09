Documento: Análise SOLID e Dependências \- Pacote com.teatroabc.enums  

***1\. CategoriaAssento***   

**Princípios SOLID:**

* SRP (Princípio da Responsabilidade Única):  
  O enum CategoriaAssento tem a única responsabilidade de definir um conjunto fixo e tipado de categorias para os assentos, associando a cada uma um nome descritivo e um preço base. Ele encapsula essa informação de categorização.  

* OCP (Princípio Aberto/Fechado):  
  O enum é fechado para modificação de suas instâncias existentes (são constantes). Se novas categorias de assento fossem necessárias, o enum seria estendido com novas constantes, o que é uma forma de extensão natural para enums.  

* LSP (Princípio da Substituição de Liskov):  
  Não diretamente aplicável no sentido tradicional de herança de classes, mas todas as instâncias do enum (PLATEIA\_A, PLATEIA\_B, etc.) são tipos de CategoriaAssento e podem ser usadas onde quer que CategoriaAssento seja esperado.  

* ISP (Princípio da Segregação de Interfaces):  
  Não aplicável, pois não implementa interfaces.  

* DIP (Princípio da Inversão de Dependência):  
  O enum depende de java.math.BigDecimal, um tipo base do JDK. Classes de domínio (como Assento) dependem deste enum, o que é aceitável, pois enums bem definidos são abstrações estáveis dentro do domínio.

**Bibliotecas Necessárias/Dependentes:**

* java.math.BigDecimal:  
  Utilizada para armazenar o precoBase de cada categoria de assento com precisão monetária.

---

***2\. StatusAssento***

**Princípios SOLID:**

* SRP (Princípio da Responsabilidade Única):  
  O enum StatusAssento tem a única responsabilidade de definir o conjunto fixo de estados possíveis para um assento (Disponível, Ocupado, Selecionado), cada um com uma descrição textual associada. Ele encapsula essa informação de estado.  

* OCP (Princípio Aberto/Fechado):  
  O enum é fechado para modificação de suas instâncias existentes (são constantes). Se um novo estado de assento fosse estritamente necessário (o que é raro para este tipo de enum), ele seria estendido com uma nova constante.  

* LSP (Princípio da Substituição de Liskov):  
  Não diretamente aplicável no sentido tradicional de herança, mas todas as instâncias são tipos de StatusAssento e podem ser usadas onde quer que StatusAssento seja esperado.  

* ISP (Princípio da Segregação de Interfaces):  
  Não aplicável.  

* DIP (Princípio da Inversão de Dependência):  
  O enum não possui dependências externas significativas além de String. Classes de domínio (Assento.java) dependem dele, o que é apropriado para um tipo de valor do domínio.

**Bibliotecas Necessárias/Dependentes:**

* Nenhuma biblioteca externa específica além das classes padrão do Java (String) é diretamente requerida por este enum.

---

***3\. StatusPagamento***

**Princípios SOLID:**

* SRP (Princípio da Responsabilidade Única):  
  O enum StatusPagamento define de forma concisa o conjunto limitado de estados (PENDENTE, PAGO, CANCELADO) para o status de um pagamento, cada um com sua descrição. Ele encapsula este vocabulário específico.  

* OCP (Princípio Aberto/Fechado):  
  O conjunto de estados é fixo e o enum é fechado para modificação de suas instâncias. Se a lógica de negócios exigisse novos estados de pagamento no futuro, o enum seria estendido com novas constantes.  

* LSP (Princípio da Substituição de Liskov):  
  Não aplicável diretamente.  

* ISP (Princípio da Segregação de Interfaces):  
  Não aplicável.  

* DIP (Princípio da Inversão de Dependência):  
  O enum é autocontido e não possui dependências externas. Classes de domínio ou serviço que gerenciam o ciclo de vida de pagamentos dependeriam deste enum.

**Bibliotecas Necessárias/Dependentes:**

* Nenhuma biblioteca externa específica além das classes padrão do Java (String) é diretamente requerida por este enum.

---

***4\. Turno***

**Princípios SOLID:**

* SRP (Princípio da Responsabilidade Única):  
  O enum Turno tem a responsabilidade clara de definir o conjunto fixo de turnos disponíveis para as apresentações (Manhã, Tarde, Noite), cada um com seu nome e horário associados.  

* OCP (Princípio Aberto/Fechado):  
  O conjunto de turnos é fixo. Se, hipoteticamente, um novo turno fosse adicionado (ex: "Matinê"), o enum seria estendido com uma nova constante, sem alterar as existentes.  

* LSP (Princípio da Substituição de Liskov):  
  Não aplicável diretamente.  

* ISP (Princípio da Segregação de Interfaces):  
  Não aplicável.  
  
* DIP (Princípio da Inversão de Dependência):  
  O enum não possui dependências externas além de String. Classes de domínio (Bilhete) e serviços (ReservaServico, AssentoRepositorio) dependem deste enum, o que é correto para um tipo de valor do domínio.

**Bibliotecas Necessárias/Dependentes:**

* Nenhuma biblioteca externa específica além das classes padrão do Java (String) é diretamente requerida por este enum.

