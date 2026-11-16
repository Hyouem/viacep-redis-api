package br.com.portfolio.viacepredis.cep;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CepServiceTest {

    private ViaCepClient client;
    private StringRedisTemplate redis;
    private ValueOperations<String, String> operations;
    private CepService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        client = mock(ViaCepClient.class);
        redis = mock(StringRedisTemplate.class);
        operations = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(operations);
        service = new CepService(client, redis, new ObjectMapper(), 24);
    }

    @Test
    void rejeitaCepInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.consultar("123")
        );
        verifyNoInteractions(client);
    }

    @Test
    void rejeitaFormatoInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.consultar("01-001000")
        );
        verifyNoInteractions(client);
    }

    @Test
    void consultaViaCepQuandoCacheVazio() {
        CepResponse expected = new CepResponse(
                "01001-000", "Praca da Se", "", "Se",
                "Sao Paulo", "SP", "Sao Paulo", "3550308"
        );

        when(client.consultar("01001000")).thenReturn(expected);

        assertEquals(expected, service.consultar("01001-000"));
        verify(client).consultar("01001000");
        verify(operations).set(
                eq("cep:01001000"),
                anyString(),
                eq(Duration.ofHours(24))
        );
    }

    @Test
    void retornaCacheSemConsultarViaCep() {
        when(operations.get("cep:01001000")).thenReturn("""
                {
                  "cep":"01001-000",
                  "logradouro":"Praca da Se",
                  "complemento":"",
                  "bairro":"Se",
                  "localidade":"Sao Paulo",
                  "uf":"SP",
                  "estado":"Sao Paulo",
                  "ibge":"3550308"
                }
                """);

        CepResponse response = service.consultar("01001000");

        assertEquals("SP", response.uf());
        verifyNoInteractions(client);
    }

    @Test
    void retornaErroQuandoCepNaoExiste() {
        when(client.consultar("99999999"))
                .thenThrow(new CepNotFoundException("99999999"));

        assertThrows(
                CepNotFoundException.class,
                () -> service.consultar("99999999")
        );

        verify(operations, never()).set(
                anyString(), anyString(), any(Duration.class)
        );
    }

    @Test
    void recuperaCacheCorrompido() {
        when(operations.get("cep:01001000"))
                .thenReturn("{json-invalido");

        CepResponse expected = new CepResponse(
                "01001-000", "Praca da Se", "", "Se",
                "Sao Paulo", "SP", "Sao Paulo", "3550308"
        );

        when(client.consultar("01001000")).thenReturn(expected);

        assertEquals(expected, service.consultar("01001000"));

        verify(redis).delete("cep:01001000");
        verify(client).consultar("01001000");
        verify(operations).set(
                eq("cep:01001000"),
                anyString(),
                eq(Duration.ofHours(24))
        );
    }

    @Test
    void propagaIndisponibilidadeDoViaCep() {
        when(client.consultar("01001000"))
                .thenThrow(
                        new ViaCepUnavailableException(
                                new RuntimeException("Timeout")
                        )
                );

        assertThrows(
                ViaCepUnavailableException.class,
                () -> service.consultar("01001000")
        );

        verify(operations, never()).set(
                anyString(), anyString(), any(Duration.class)
        );
    }
}
