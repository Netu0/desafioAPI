package com.oswaldo.desafio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoDTO(
        Long idTransacao,
        Long idConta,
        BigDecimal valor,
        LocalDateTime dataTransacao,
        String tipo
) {}
