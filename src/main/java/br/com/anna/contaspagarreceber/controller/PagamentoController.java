package br.com.anna.contaspagarreceber.controller;

import br.com.anna.contaspagarreceber.dto.QuitacaoDTO;
import br.com.anna.contaspagarreceber.enums.StatusConta;
import br.com.anna.contaspagarreceber.model.Pagamento;
import br.com.anna.contaspagarreceber.service.PagamentoService;
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
@RequestMapping("/api/pagamentos")
@Tag(name = "Contas a Pagar", description = "Cadastro, consulta e quitação de contas a pagar")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista contas a pagar", description = "Permite filtrar por situação e período de vencimento")
    public ResponseEntity<List<Pagamento>> listar(
            @Parameter(description = "Situação da conta")
            @RequestParam(required = false) StatusConta status,
            @Parameter(description = "Data inicial do vencimento (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Data final do vencimento (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listar(status, inicio, fim));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma conta a pagar pelo título")
    public ResponseEntity<Pagamento> buscar(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma conta a pagar")
    public ResponseEntity<Pagamento> salvar(@Valid @RequestBody Pagamento objeto) {
        return ResponseEntity.status(201).body(service.salvar(objeto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma conta a pagar")
    public ResponseEntity<Pagamento> atualizar(@PathVariable("id") String id, @Valid @RequestBody Pagamento objeto) {
        return ResponseEntity.ok(service.atualizar(id, objeto));
    }

    @PatchMapping("/{id}/quitar")
    @Operation(summary = "Registra o pagamento de uma conta a pagar")
    public ResponseEntity<Pagamento> quitar(@PathVariable("id") String id, @Valid @RequestBody QuitacaoDTO dados) {
        return ResponseEntity.ok(service.quitar(id, dados));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela uma conta aberta ou vencida, preservando o registro")
    public ResponseEntity<Pagamento> cancelar(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma conta a pagar")
    public ResponseEntity<Void> excluir(@PathVariable("id") String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
