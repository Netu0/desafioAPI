package com.oswaldo.desafio.service;

import com.oswaldo.desafio.dto.ContaDTO;
import com.oswaldo.desafio.dto.CreateContaDTO;
import com.oswaldo.desafio.exception.ResourceNotFoundException;
import com.oswaldo.desafio.model.Conta;
import com.oswaldo.desafio.repository.ContaRepository;
import com.oswaldo.desafio.repository.PessoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final PessoaRepository pessoaRepository;

    public ContaService(ContaRepository contaRepository,
                        PessoaRepository pessoaRepository) {
        this.contaRepository = contaRepository;
        this.pessoaRepository = pessoaRepository;
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

        return new ContaDTO(saved.getIdConta(), saved.getIdPessoa(), saved.getSaldo(),
                saved.getLimiteSaqueDiario(), saved.getFlagAtivo(), saved.getTipoConta(),
                saved.getDataCriacao());
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
        return new ContaDTO(saved.getIdConta(), saved.getIdPessoa(), saved.getSaldo(),
                saved.getLimiteSaqueDiario(), saved.getFlagAtivo(),
                saved.getTipoConta(), saved.getDataCriacao());
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarSaldo(Long idConta) {
        Conta c = contaRepository.findById(idConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + idConta));
        return c.getSaldo();
    }
}
