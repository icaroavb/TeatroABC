// Pacote: com.teatroabc.aplicacao.excecoes (ou similar)
package com.teatroabc.aplicacao.excecoes;

public class ReservaInvalidaException extends Exception {
  public ReservaInvalidaException(String message) {
    super(message);
  }

  public ReservaInvalidaException(String message, Throwable cause) {
    super(message, cause);
  }
}