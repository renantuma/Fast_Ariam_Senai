package com.fastariam.service;

/**
 * Erro de leitura de PDF (arquivo vazio, corrompido, protegido por senha etc.)
 * cuja mensagem é segura para exibir diretamente ao usuário.
 */
public class PdfExtracaoException extends RuntimeException {
    public PdfExtracaoException(String message) {
        super(message);
    }
}
