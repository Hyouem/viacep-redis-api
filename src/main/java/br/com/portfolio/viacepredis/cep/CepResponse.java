package br.com.portfolio.viacepredis.cep;

public record CepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String ibge
) {}