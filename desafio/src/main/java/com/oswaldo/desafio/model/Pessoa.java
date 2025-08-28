package com.oswaldo.desafio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "TB_PESSOA", schema = "dbo")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pessoa_seq")
    @SequenceGenerator(name = "pessoa_seq", sequenceName = "seq_pessoa", allocationSize = 1)
    @Column(name = "IDPESSOA")
    private Long idPessoa;


    @Column(nullable = false, length = 100, name = "NOME")
    private String nome;

    @Column(nullable = false, unique = true, length = 11, name = "CPF")
    private String cpf;

    @Column(nullable = false, name = "DATANASCIMENTO")
    private LocalDate dataNascimento;

    // Getters e Setters
    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
