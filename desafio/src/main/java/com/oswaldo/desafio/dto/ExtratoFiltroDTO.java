package com.oswaldo.desafio.dto;

import java.time.LocalDate;

public record ExtratoFiltroDTO(
        LocalDate start,
        LocalDate end
) {}
