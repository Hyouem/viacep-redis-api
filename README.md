# ViaCEP Redis API

API REST em Java 25 e Spring Boot com cache Redis.

## Executar

```powershell
docker compose up -d
mvn spring-boot:run
```

## Endpoint

GET http://localhost:8082/api/ceps/01001000

## Funcionalidades

- Consulta ViaCEP
- Validacao de CEP
- Cache Redis com TTL de 24 horas
- HTTP 400, 404 e 503
- Testes automatizados