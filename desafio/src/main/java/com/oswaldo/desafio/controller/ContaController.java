package com.oswaldo.desafio.controller;

import com.oswaldo.desafio.dto.ContaDTO;
import com.oswaldo.desafio.dto.CreateContaDTO;
import com.oswaldo.desafio.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public ResponseEntity<ContaDTO> criarConta(@RequestBody CreateContaDTO dto) {
        return ResponseEntity.ok(contaService.criarConta(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<ContaDTO> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.bloquearConta(id));
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<BigDecimal> saldo(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.consultarSaldo(id));
    }
}
