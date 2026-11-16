package br.com.portfolio.viacepredis.cep;

public class ViaCepUnavailableException extends RuntimeException {
    public ViaCepUnavailableException(Throwable cause) {
        super("Servico ViaCEP indisponivel", cause);
    }
}