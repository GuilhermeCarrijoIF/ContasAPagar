package br.com.anna.contaspagarreceber.controller;

import br.com.anna.contaspagarreceber.dto.QuitacaoDTO;
import br.com.anna.contaspagarreceber.enums.StatusConta;
import br.com.anna.contaspagarreceber.model.Recebimento;
import br.com.anna.contaspagarreceber.service.RecebimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recebimentos")
@Tag(name = "Contas a Receber", description = "Cadastro, consulta e registro de recebimento de contas a receber")
public class RecebimentoController {

    private final RecebimentoService service;

    public RecebimentoController(RecebimentoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista contas a receber", description = "Permite filtrar por situação e período de vencimento")
    public ResponseEntity<List<Recebimento>> listar(
            @Parameter(description = "Situação da conta")
            @RequestParam(required = false) StatusConta status,
            @Parameter(description = "Data inicial do vencimento (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Data final do vencimento (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listar(status, inicio, fim));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma conta a receber pelo título")
    public ResponseEntity<Recebimento> buscar(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma conta a receber")
    public ResponseEntity<Recebimento> salvar(@Valid @RequestBody Recebimento objeto) {
        return ResponseEntity.status(201).body(service.salvar(objeto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma conta a receber")
    public ResponseEntity<Recebimento> atualizar(@PathVariable("id") String id, @Valid @RequestBody Recebimento objeto) {
        return ResponseEntity.ok(service.atualizar(id, objeto));
    }

    @PatchMapping("/{id}/quitar")
    @Operation(summary = "Registra o recebimento de uma conta a receber")
    public ResponseEntity<Recebimento> quitar(@PathVariable("id") String id, @Valid @RequestBody QuitacaoDTO dados) {
        return ResponseEntity.ok(service.quitar(id, dados));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela uma conta aberta ou vencida, preservando o registro")
    public ResponseEntity<Recebimento> cancelar(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma conta a receber")
    public ResponseEntity<Void> excluir(@PathVariable("id") String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
