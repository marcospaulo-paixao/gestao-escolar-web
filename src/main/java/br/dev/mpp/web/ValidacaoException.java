package br.dev.mpp.web;

/**
 * Regra de negócio violada. A mensagem é exibida para o usuário.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
