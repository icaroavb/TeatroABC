package com.teatroabc.aplicacao.excecoes; // Assumindo que está no pacote de exceções da aplicação

/**
 * Exceção customizada para indicar que uma tentativa de criar ou processar uma reserva
 * (resultando em um bilhete) falhou devido a uma condição de negócio inválida.
 * Exemplos incluem assentos selecionados que não estão mais disponíveis,
 * inconsistências nos dados da reserva que não são erros de formato básico,
 * ou outras regras de negócio não satisfeitas durante o processo de reserva.
 * <p>
 * Esta é uma exceção checada (checked exception), estendendo {@link Exception},
 * o que significa que os métodos que podem lançá-la devem explicitamente
 * declará-la em sua cláusula {@code throws} ou tratá-la.
 */
public class ReservaInvalidaException extends Exception {

    /**
     * Constrói uma nova {@code ReservaInvalidaException} com a mensagem de detalhe especificada.
     *
     * @param message A mensagem de detalhe. A mensagem de detalhe é salva para
     *                recuperação posterior pelo método {@link Throwable#getMessage()}.
     */
    public ReservaInvalidaException(String message) {
        super(message);
    }

    /**
     * Constrói uma nova {@code ReservaInvalidaException} com a mensagem de detalhe
     * e causa especificadas.
     * <p>
     * Nota: A mensagem de detalhe associada a {@code cause} não é automaticamente
     * incorporada na mensagem de detalhe desta exceção.
     *
     * @param message A mensagem de detalhe (que é salva para recuperação posterior
     *                pelo método {@link Throwable#getMessage()}).
     * @param cause A causa da exceção (que é salva para recuperação posterior pelo
     *              método {@link Throwable#getCause()}). Um valor {@code null} é
     *              permitido, e indica que a causa é inexistente ou desconhecida.
     */
    public ReservaInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}