package com.oswaldo.desafio.service;

import com.oswaldo.desafio.dto.PessoaDTO;
import com.oswaldo.desafio.exception.ResourceNotFoundException;
import com.oswaldo.desafio.model.Pessoa;
import com.oswaldo.desafio.repository.PessoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PessoaService {

    private final PessoaRepository repository;

    public PessoaService(PessoaRepository repository) {
        this.repository = repository;
    }

    public List<PessoaDTO> listar() {
        return repository.findAll().stream()
                .map(p -> new PessoaDTO(p.getIdPessoa(), p.getNome(), p.getCpf(), p.getDataNascimento()))
                .toList();
    }

    public PessoaDTO buscarPorId(Long id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada!"));
        return new PessoaDTO(pessoa.getIdPessoa(), pessoa.getNome(), pessoa.getCpf(), pessoa.getDataNascimento());
    }

    public PessoaDTO salvar(PessoaDTO dto) {
        if (repository.existsByCpf(dto.cpf())) {
            throw new IllegalArgumentException("CPF já cadastrado!");
        }
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setCpf(dto.cpf());
        pessoa.setDataNascimento(dto.dataNascimento());

        Pessoa saved = repository.save(pessoa);
        return new PessoaDTO(saved.getIdPessoa(), saved.getNome(), saved.getCpf(), saved.getDataNascimento());
    }

    public PessoaDTO atualizar(Long id, PessoaDTO dto) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada!"));
        pessoa.setNome(dto.nome());
        pessoa.setCpf(dto.cpf());
        pessoa.setDataNascimento(dto.dataNascimento());
        Pessoa updated = repository.save(pessoa);
        return new PessoaDTO(updated.getIdPessoa(), updated.getNome(), updated.getCpf(), updated.getDataNascimento());
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Pessoa não encontrada!");
        }
        repository.deleteById(id);
    }
}
