package com.oswaldo.desafio.dto;

import java.time.LocalDate;

public record PessoaDTO(
        Long idPessoa,
        String nome,
        String cpf,
        LocalDate dataNascimento
) {}
