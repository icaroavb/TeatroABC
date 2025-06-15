***ReservaInvalidaException.java***

**Princípios SOLID**

- SRP (Princípio da Responsabilidade Única):

A classe ReservaInvalidaException possui a única responsabilidade de representar uma falha específica de regra de negócio durante o processo de criação ou validação de uma reserva. Ela sinaliza que uma operação de reserva não pôde ser concluída devido a condições inválidas no domínio ou na lógica de aplicação.

- OCP (Princípio Aberto/Fechado):

A classe é fechada para modificação em seu propósito fundamental. Se fossem necessárias distinções mais granulares de erros de reserva (ex: AssentoNaoDisponivelException, LimiteDeAssentosExcedidoException), novas classes de exceção poderiam ser criadas, possivelmente herdando de ReservaInvalidaException para permitir tratamento agrupado.

- LSP (Princípio da Substituição de Liskov):

Como uma subclasse de java.lang.Exception, ela pode ser utilizada em qualquer contexto onde uma Exception ou Throwable é esperada. Blocos catch podem tratá-la especificamente ou de forma mais genérica.

- ISP (Princípio da Segregação de Interfaces):

Não aplicável diretamente, pois é uma classe de exceção e não implementa interfaces de negócio.

- DIP (Princípio da Inversão de Dependência):

Não aplicável diretamente no contexto desta classe de exceção simples, que herda de java.lang.Exception.

**Bibliotecas Necessárias/Dependentes**

- Nenhuma biblioteca externa específica além das classes base do Java (java.lang.Exception, java.lang.String, java.lang.Throwable) é diretamente requerida por esta classe de exceção.

**Análise sob a Perspectiva da Arquitetura Hexagonal**

- Natureza da Classe:

ReservaInvalidaException é uma exceção de negócio (ou de aplicação). Ela é lançada pela camada de aplicação (Serviços) para indicar que uma operação solicitada (neste caso, criarReserva) não pôde ser completada devido a uma violação de regras de negócio ou estado inválido do domínio (ex: assento já ocupado).

- Acoplamento:

Totalmente desacoplada de tecnologias de UI, persistência ou frameworks.

- Localização e Uso na Arquitetura Hexagonal:

Lançada pelo Hexágono: É lançada de dentro do núcleo da aplicação (especificamente pelos Serviços de Aplicação, como ReservaServico).
Tratada por Adaptadores de Entrada: Adaptadores primários (como as classes da UI Swing) são responsáveis por capturar (catch) esta exceção e traduzi-la em uma mensagem apropriada ou ação para o usuário (ex: exibir um JOptionPane com a mensagem de erro).
Isso permite que o núcleo da aplicação comunique falhas de negócio de forma clara, sem se preocupar com os detalhes de como essa falha será apresentada ao usuário.

- Conformidade com a Arquitetura Hexagonal:

Sim, está totalmente condizente. O uso de exceções customizadas para erros de negócio é uma prática recomendada na arquitetura hexagonal, pois permite que o domínio/aplicação sinalize problemas de forma explícita para as camadas externas (adaptadores).

***ClienteJaCadastradoException***

**Princípios SOLID**

- SRP (Princípio da Responsabilidade Única):

A classe ClienteJaCadastradoException tem a responsabilidade singular e bem definida de representar a condição de erro específica em que uma tentativa de cadastro de cliente falha devido à pré-existência de um cliente com o mesmo CPF. Ela encapsula a semântica deste erro de negócio.

- OCP (Princípio Aberto/Fechado):

Como uma classe de exceção específica, ela é fechada para modificação em seu propósito fundamental. Se outros tipos de erros de validação de cliente fossem necessários (ex: CPFInvalidoException), seriam criadas novas classes de exceção, em vez de alterar esta.

- LSP (Princípio da Substituição de Liskov):

Sendo uma subclasse de java.lang.Exception, pode ser capturada e tratada por blocos catch que esperam ClienteJaCadastradoException, Exception, ou Throwable, mantendo o comportamento esperado de uma exceção.

- ISP (Princípio da Segregação de Interfaces):

Não aplicável diretamente, pois é uma classe de exceção e não implementa interfaces de negócio.

- DIP (Princípio da Inversão de Dependência):

Não aplicável diretamente no contexto desta classe de exceção simples, que herda de java.lang.Exception.

**Bibliotecas Necessárias/Dependentes**

- Nenhuma biblioteca externa específica além das classes base do Java (java.lang.Exception, java.lang.String, java.lang.Throwable) é diretamente requerida por esta classe de exceção.