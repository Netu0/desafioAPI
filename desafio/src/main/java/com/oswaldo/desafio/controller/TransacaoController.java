package com.oswaldo.desafio.controller;

import com.oswaldo.desafio.dto.OperacaoDTO;
import com.oswaldo.desafio.dto.TransacaoDTO;
import com.oswaldo.desafio.service.TransacaoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;  

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping("/{idConta}/deposito")
    public ResponseEntity<TransacaoDTO> deposito(@PathVariable Long idConta,
            @RequestBody OperacaoDTO dto) {
        return ResponseEntity.ok(transacaoService.depositar(idConta, dto));
    }

    @PostMapping("/{idConta}/saque")
    public ResponseEntity<TransacaoDTO> saque(@PathVariable Long idConta,
            @RequestBody OperacaoDTO dto) {
        return ResponseEntity.ok(transacaoService.sacar(idConta, dto));
    }

    @GetMapping("/{idConta}/extrato")
    public ResponseEntity<List<TransacaoDTO>> extrato(
            @PathVariable Long idConta,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        LocalDateTime s = null;
        LocalDateTime e = null;
        if (start != null && end != null) {
            s = start.atStartOfDay();
            e = end.atTime(LocalTime.MAX);
        }

        return ResponseEntity.ok(transacaoService.extrato(idConta, s, e));
    }
}
