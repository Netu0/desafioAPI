package com.oswaldo.desafio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContaDTO(
        Long idConta,
        Long idPessoa,
        BigDecimal saldo,
        BigDecimal limiteSaqueDiario,
        String flagAtivo,
        Integer tipoConta,
        LocalDate dataCriacao
) {}
