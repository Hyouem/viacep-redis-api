package br.com.portfolio.viacepredis.cep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CepService {

    private final ViaCepClient client;
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final Duration ttl;

    public CepService(
            ViaCepClient client,
            StringRedisTemplate redis,
            ObjectMapper mapper,
            @Value("${app.cache.ttl-hours}") long ttlHours
    ) {
        this.client = client;
        this.redis = redis;
        this.mapper = mapper;
        this.ttl = Duration.ofHours(ttlHours);
    }

    public CepResponse consultar(String input) {
        if (input == null || !input.matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP deve conter 8 digitos");
        }

        String cep = input.replace("-", "");
        String key = "cep:" + cep;

        String cached = redis.opsForValue().get(key);

        if (cached != null) {
            try {
                return mapper.readValue(cached, CepResponse.class);
            } catch (JsonProcessingException exception) {
                redis.delete(key);
            }
        }

        CepResponse response = client.consultar(cep);

        try {
            redis.opsForValue().set(
                    key,
                    mapper.writeValueAsString(response),
                    ttl
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Falha ao serializar CEP", exception);
        }

        return response;
    }
}