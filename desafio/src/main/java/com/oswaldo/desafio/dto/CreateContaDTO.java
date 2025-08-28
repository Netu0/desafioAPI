package com.oswaldo.desafio.dto;

import java.math.BigDecimal;

public record CreateContaDTO(
        Long idPessoa,
        BigDecimal saldoInicial,
        BigDecimal limiteSaqueDiario,
        Integer tipoConta
) {}
