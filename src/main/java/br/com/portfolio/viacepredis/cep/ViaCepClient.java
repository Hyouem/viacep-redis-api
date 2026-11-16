package br.com.portfolio.viacepredis.cep;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ViaCepClient {

    private final RestClient client;

    public ViaCepClient(@Value("${app.viacep.base-url}") String baseUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));

        client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public CepResponse consultar(String cep) {
        try {
            ViaCepResponse response = client.get()
                    .uri("/ws/{cep}/json/", cep)
                    .retrieve()
                    .body(ViaCepResponse.class);

            if (response == null || Boolean.TRUE.equals(response.erro())) {
                throw new CepNotFoundException(cep);
            }

            if (response.cep() == null) {
                throw new IllegalStateException("Resposta incompleta do ViaCEP");
            }

            return response.toResponse();
        } catch (CepNotFoundException exception) {
            throw exception;
        } catch (RestClientException | IllegalStateException exception) {
            throw new ViaCepUnavailableException(exception);
        }
    }
}