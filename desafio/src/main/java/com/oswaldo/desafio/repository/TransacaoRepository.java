package com.oswaldo.desafio.repository;

import com.oswaldo.desafio.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByIdContaOrderByDataTransacaoDesc(Long idConta);

    List<Transacao> findByIdContaAndDataTransacaoBetweenOrderByDataTransacaoDesc(Long idConta,
            LocalDateTime start,
            LocalDateTime end);

    List<Transacao> findByIdContaAndTipoAndDataTransacaoBetween(Long idConta, String tipo,
            LocalDateTime start, LocalDateTime end);
}
