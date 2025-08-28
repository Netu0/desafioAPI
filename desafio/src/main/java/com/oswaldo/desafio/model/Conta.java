package com.oswaldo.desafio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TB_CONTA", schema = "dbo")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conta_seq")
    @SequenceGenerator(name = "conta_seq", sequenceName = "seq_conta", allocationSize = 1)
    @Column(name = "IDCONTA")
    private Long idConta;

    @Column(name = "IDPESSOA", nullable = false)
    private Long idPessoa;

    @Column(name = "SALDO", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "LIMITESAQUEDIARIO", precision = 15, scale = 2)
    private BigDecimal limiteSaqueDiario;

    @Column(name = "FLAGATIVO", nullable = false, length = 1)
    private String flagAtivo; // 'S' ou 'N'

    @Column(name = "TIPOCONTA", nullable = false)
    private Integer tipoConta;

    @Column(name = "DATACRIACAO", nullable = false)
    private LocalDate dataCriacao;

    // getters e setters
    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getLimiteSaqueDiario() {
        return limiteSaqueDiario;
    }

    public void setLimiteSaqueDiario(BigDecimal limiteSaqueDiario) {
        this.limiteSaqueDiario = limiteSaqueDiario;
    }

    public String getFlagAtivo() {
        return flagAtivo;
    }

    public void setFlagAtivo(String flagAtivo) {
        this.flagAtivo = flagAtivo;
    }

    public Integer getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(Integer tipoConta) {
        this.tipoConta = tipoConta;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
