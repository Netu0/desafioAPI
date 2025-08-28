package com.oswaldo.desafio.controller;

import com.oswaldo.desafio.dto.*;
import com.oswaldo.desafio.service.ContaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContaDTO> criarConta(@RequestBody CreateContaDTO dto) {
        return ResponseEntity.ok(service.criarConta(dto));
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<TransacaoDTO> deposito(@PathVariable Long id, @RequestBody OperacaoDTO dto) {
        return ResponseEntity.ok(service.depositar(id, dto));
    }

    @PostMapping("/{id}/saque")
    public ResponseEntity<TransacaoDTO> saque(@PathVariable Long id, @RequestBody OperacaoDTO dto) {
        return ResponseEntity.ok(service.sacar(id, dto));
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<BigDecimal> saldo(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarSaldo(id));
    }

    @PostMapping("/{id}/bloquear")
    public ResponseEntity<ContaDTO> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(service.bloquearConta(id));
    }

    @GetMapping("/{id}/extrato")
    public ResponseEntity<List<TransacaoDTO>> extrato(
            @PathVariable Long id,
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        LocalDateTime s = null;
        LocalDateTime e = null;
        if (start != null && end != null) {
            s = start.atStartOfDay();
            e = end.atTime(LocalTime.MAX);
        }
        return ResponseEntity.ok(service.extrato(id, s, e));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
