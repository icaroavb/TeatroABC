package com.teatroabc.aplicacao.excecoes;

/**
 * Exceção customizada para indicar que uma tentativa de cadastrar um cliente
 * falhou porque já existe um cliente com o mesmo identificador (CPF) no sistema.
 * Estende {@link Exception}, tornando-a uma exceção checada (checked exception),
 * o que força os métodos que podem lançá-la a declará-la em sua assinatura
 * ou a tratá-la explicitamente.
 */
public class ClienteJaCadastradoException extends Exception {

    /**
     * Constrói uma nova exceção com a mensagem de detalhe especificada.
     * @param message A mensagem de detalhe. A mensagem de detalhe é salva para
     *                recuperação posterior pelo método {@link Throwable#getMessage()}.
     */
    public ClienteJaCadastradoException(String message) {
        super(message);
    }

    /**
     *
     * @param message A mensagem de detalhe (que é salva para recuperação posterior
     *                pelo método {@link Throwable#getMessage()}).
     * @param cause A causa (que é salva para recuperação posterior pelo método
     *              {@link Throwable#getCause()}). (Um valor {@code null} é permitido,
     *              e indica que a causa é inexistente ou desconhecida.)
     */
    public ClienteJaCadastradoException(String message, Throwable cause) {
        super(message, cause);
    }
}