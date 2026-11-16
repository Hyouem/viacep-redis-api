package br.com.portfolio.viacepredis.cep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String ibge,
        Boolean erro
) {
    public CepResponse toResponse() {
        return new CepResponse(
                cep, logradouro, complemento, bairro,
                localidade, uf, estado, ibge
        );
    }
}