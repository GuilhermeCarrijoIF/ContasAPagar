package br.com.anna.contaspagarreceber.controller;

import br.com.anna.contaspagarreceber.model.PlanoContas;
import br.com.anna.contaspagarreceber.service.PlanoContasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plano-contas")
@Tag(name = "PlanoContas", description = "Operações de PlanoContas")
public class PlanoContasController {

    private final PlanoContasService service;

    public PlanoContasController(PlanoContasService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos os registros")
    public ResponseEntity<List<PlanoContas>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro pelo identificador")
    public ResponseEntity<PlanoContas> buscar(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo registro")
    public ResponseEntity<PlanoContas> salvar(@Valid @RequestBody PlanoContas objeto) {
        return ResponseEntity.status(201).body(service.salvar(objeto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um registro")
    public ResponseEntity<PlanoContas> atualizar(@PathVariable("id") Long id, @Valid @RequestBody PlanoContas objeto) {
        return ResponseEntity.ok(service.atualizar(id, objeto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um registro")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
