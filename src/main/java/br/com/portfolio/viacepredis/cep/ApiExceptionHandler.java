package br.com.portfolio.viacepredis.cep;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail invalid(IllegalArgumentException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail(exception.getMessage());
        return detail;
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ProblemDetail notFound(CepNotFoundException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setDetail(exception.getMessage());
        return detail;
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ProblemDetail unavailable(ViaCepUnavailableException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        detail.setDetail(exception.getMessage());
        return detail;
    }
}