package com.oswaldo.desafio.service;

import com.oswaldo.desafio.dto.OperacaoDTO;
import com.oswaldo.desafio.dto.TransacaoDTO;
import com.oswaldo.desafio.exception.*;
import com.oswaldo.desafio.model.Conta;
import com.oswaldo.desafio.model.Transacao;
import com.oswaldo.desafio.repository.ContaRepository;
import com.oswaldo.desafio.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(ContaRepository contaRepository,
                            TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public TransacaoDTO depositar(Long idConta, OperacaoDTO dto) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + idConta));

        validarContaAtiva(conta);
        validarValor(dto.valor());

        conta.setSaldo(conta.getSaldo().add(dto.valor()));
        contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setIdConta(idConta);
        transacao.setValor(dto.valor());
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTipo("DEPOSITO");

        Transacao saved = transacaoRepository.save(transacao);

        return toDTO(saved);
    }

    @Transactional
    public TransacaoDTO sacar(Long idConta, OperacaoDTO dto) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + idConta));

        validarContaAtiva(conta);
        validarValor(dto.valor());

        if (conta.getSaldo().compareTo(dto.valor()) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente.");
        }

        // limite diário
        BigDecimal limite = conta.getLimiteSaqueDiario() == null ? BigDecimal.ZERO : conta.getLimiteSaqueDiario();
        if (limite.compareTo(BigDecimal.ZERO) > 0) {
            LocalDate today = LocalDate.now();
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end = today.atTime(LocalTime.MAX);

            List<Transacao> saquesHoje = transacaoRepository
                    .findByIdContaAndTipoAndDataTransacaoBetween(idConta, "SAQUE", start, end);

            BigDecimal totalSacadoHoje = saquesHoje.stream()
                    .map(Transacao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalSacadoHoje.add(dto.valor()).compareTo(limite) > 0) {
                throw new BusinessException("Limite diário de saque excedido.");
            }
        }

        conta.setSaldo(conta.getSaldo().subtract(dto.valor()));
        contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setIdConta(idConta);
        transacao.setValor(dto.valor());
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTipo("SAQUE");

        Transacao saved = transacaoRepository.save(transacao);

        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<TransacaoDTO> extrato(Long idConta, LocalDateTime start, LocalDateTime end) {
        if (!contaRepository.existsById(idConta)) {
            throw new ResourceNotFoundException("Conta não encontrada: " + idConta);
        }

        List<Transacao> list = (start != null && end != null)
                ? transacaoRepository.findByIdContaAndDataTransacaoBetweenOrderByDataTransacaoDesc(idConta, start, end)
                : transacaoRepository.findByIdContaOrderByDataTransacaoDesc(idConta);

        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private void validarContaAtiva(Conta conta) {
        if (!"S".equalsIgnoreCase(conta.getFlagAtivo())) {
            throw new AccountBlockedException("Conta está bloqueada.");
        }
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor inválido.");
        }
    }

    private TransacaoDTO toDTO(Transacao t) {
        return new TransacaoDTO(t.getIdTransacao(), t.getIdConta(),
                t.getValor(), t.getDataTransacao(), t.getTipo());
    }
}
