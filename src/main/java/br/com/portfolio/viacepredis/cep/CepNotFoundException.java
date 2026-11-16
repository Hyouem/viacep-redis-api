package br.com.portfolio.viacepredis.cep;

public class CepNotFoundException extends RuntimeException {
    public CepNotFoundException(String cep) {
        super("CEP nao encontrado: " + cep);
    }
}