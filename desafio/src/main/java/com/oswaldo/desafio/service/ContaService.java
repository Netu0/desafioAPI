package com.oswaldo.desafio.service;

import com.oswaldo.desafio.dto.*;
import com.oswaldo.desafio.exception.*;
import com.oswaldo.desafio.model.Conta;
import com.oswaldo.desafio.model.Transacao;
import com.oswaldo.desafio.repository.ContaRepository;
import com.oswaldo.desafio.repository.PessoaRepository;
import com.oswaldo.desafio.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final PessoaRepository pessoaRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository,
                        PessoaRepository pessoaRepository,
                        TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.pessoaRepository = pessoaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public ContaDTO criarConta(CreateContaDTO dto) {
        if (dto.idPessoa() == null || !pessoaRepository.existsById(dto.idPessoa())) {
            throw new ResourceNotFoundException("Pessoa não encontrada para id: " + dto.idPessoa());
        }

        Conta conta = new Conta();
        conta.setIdPessoa(dto.idPessoa());
        conta.setSaldo(dto.saldoInicial() == null ? BigDecimal.ZERO : dto.saldoInicial());
        conta.setLimiteSaqueDiario(dto.limiteSaqueDiario() == null ? BigDecimal.ZERO : dto.limiteSaqueDiario());
        conta.setFlagAtivo("S");
        conta.setTipoConta(dto.tipoConta() == null ? 1 : dto.tipoConta());
        conta.setDataCriacao(LocalDate.now());

        Conta saved = contaRepository.save(conta);

        // se saldo inicial > 0, registrar transação de depósito
        if (saved.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            Transacao t = new Transacao();
            t.setIdConta(saved.getIdConta());
            t.setValor(saved.getSaldo());
            t.setDataTransacao(LocalDateTime.now());
            t.setTipo("DEPOSITO");
            transacaoRepository.save(t);
        }

        return new ContaDTO(saved.getIdConta(), saved.getIdPessoa(), saved.getSaldo(),
                saved.getLimiteSaqueDiario(), saved.getFlagAtivo(), saved.getTipoConta(),
                saved.getDataCriacao());
    }

    @Transactional
    public TransacaoDTO depositar(Long idConta, OperacaoDTO dto) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + idConta));

        if (!"S".equalsIgnoreCase(conta.getFlagAtivo())) {
            throw new AccountBlockedException("Conta está bloqueada.");
        }

        if (dto.valor() == null || dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor de depósito inválido.");
        }

        conta.setSaldo(conta.getSaldo().add(dto.valor()));
        contaRepository.save(conta);

        Transacao t = new Transacao();
        t.setIdConta(idConta);
        t.setValor(dto.valor());
        t.setDataTransacao(LocalDateTime.now());
        t.setTipo("DEPOSITO");
        Transacao saved = transacaoRepository.save(t);

        return new TransacaoDTO(saved.getIdTransacao(), saved.getIdConta(), saved.getValor(),
                saved.getDataTransacao(), saved.getTipo());
    }

    @Transactional
    public TransacaoDTO sacar(Long idConta, OperacaoDTO dto) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + idConta));

        if (!"S".equalsIgnoreCase(conta.getFlagAtivo())) {
            throw new AccountBlockedException("Conta está bloqueada.");
        }

        if (dto.valor() == null || dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor de saque inválido.");
        }

        // verificar saldo
        if (conta.getSaldo().compareTo(dto.valor()) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente.");
        }

        // verificar limite diário
        BigDecimal limite = conta.getLimiteSaqueDiario() == null ? BigDecimal.ZERO : conta.getLimiteSaqueDiario();
        if (limite.compareTo(BigDecimal.ZERO) > 0) {
            LocalDate today = LocalDate.now();
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end = today.atTime(LocalTime.MAX);

            List<Transacao> saquesHoje = transacaoRepository.findByIdContaAndTipoAndDataTransacaoBetween(
                    idConta, "SAQUE", start, end);

            BigDecimal totalSacadoHoje = saquesHoje.stream()
                    .map(Transacao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalSacadoHoje.add(dto.valor()).compareTo(limite) > 0) {
                throw new BusinessException("Limite diário de saque excedido.");
            }
        }

        // efetivar saque
        conta.setSaldo(conta.getSaldo().subtract(dto.valor()));
        contaRepository.save(conta);

        Transacao t = new Transacao();
        t.setIdConta(idConta);
        t.setValor(dto.valor());
        t.setDataTransacao(LocalDateTime.now());
        t.setTipo("SAQUE");
        Transacao saved = transacaoRepository.save(t);

        return new TransacaoDTO(saved.getIdTransacao(), saved.getIdConta(), saved.getValor(),
                saved.getDataTransacao(), saved.getTipo());
    }

    @Transactional(readOnly = true)
    public ContaDTO buscarPorId(Long id) {
        Conta c = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + id));
        return new ContaDTO(c.getIdConta(), c.getIdPessoa(), c.getSaldo(), c.getLimiteSaqueDiario(),
                c.getFlagAtivo(), c.getTipoConta(), c.getDataCriacao());
    }

    @Transactional
    public ContaDTO bloquearConta(Long idConta) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + idConta));
        conta.setFlagAtivo("N");
        Conta saved = contaRepository.save(conta);
        return new ContaDTO(saved.getIdConta(), saved.getIdPessoa(), saved.getSaldo(), saved.getLimiteSaqueDiario(),
                saved.getFlagAtivo(), saved.getTipoConta(), saved.getDataCriacao());
    }

    @Transactional(readOnly = true)
    public List<TransacaoDTO> extrato(Long idConta, LocalDateTime start, LocalDateTime end) {
        if (!contaRepository.existsById(idConta)) {
            throw new ResourceNotFoundException("Conta não encontrada: " + idConta);
        }

        List<Transacao> list;
        if (start != null && end != null) {
            list = transacaoRepository.findByIdContaAndDataTransacaoBetweenOrderByDataTransacaoDesc(idConta, start, end);
        } else {
            list = transacaoRepository.findByIdContaOrderByDataTransacaoDesc(idConta);
        }

        return list.stream()
                .map(t -> new TransacaoDTO(t.getIdTransacao(), t.getIdConta(), t.getValor(), t.getDataTransacao(), t.getTipo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarSaldo(Long idConta) {
        Conta c = contaRepository.findById(idConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + idConta));
        return c.getSaldo();
    }
}
