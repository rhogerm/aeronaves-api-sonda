package com.sonda.aeronaves.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AeronaveNaoEncontradaException.class)
    public ResponseEntity<ApiError> tratarNaoEncontrada(AeronaveNaoEncontradaException ex, HttpServletRequest req) {
        ApiError erro = new ApiError(HttpStatus.NOT_FOUND.value(), "Nao encontrado", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        ApiError erro = new ApiError(HttpStatus.BAD_REQUEST.value(), "Dados invalidos",
                "Um ou mais campos nao passaram na validacao", req.getRequestURI(), detalhes);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> tratarArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest req) {
        ApiError erro = new ApiError(HttpStatus.BAD_REQUEST.value(), "Requisicao invalida", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> tratarErroGenerico(Exception ex, HttpServletRequest req) {
        ApiError erro = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno",
                "Ocorreu um erro inesperado ao processar a requisicao", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
