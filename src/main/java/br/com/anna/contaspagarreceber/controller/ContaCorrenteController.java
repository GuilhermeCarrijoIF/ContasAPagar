package br.com.anna.contaspagarreceber.controller;

import br.com.anna.contaspagarreceber.model.ContaCorrente;
import br.com.anna.contaspagarreceber.service.ContaCorrenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas-correntes")
@Tag(name = "ContaCorrente", description = "Operações de ContaCorrente")
public class ContaCorrenteController {

    private final ContaCorrenteService service;

    public ContaCorrenteController(ContaCorrenteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos os registros")
    public ResponseEntity<List<ContaCorrente>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro pelo identificador")
    public ResponseEntity<ContaCorrente> buscar(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo registro")
    public ResponseEntity<ContaCorrente> salvar(@Valid @RequestBody ContaCorrente objeto) {
        return ResponseEntity.status(201).body(service.salvar(objeto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um registro")
    public ResponseEntity<ContaCorrente> atualizar(@PathVariable("id") String id, @Valid @RequestBody ContaCorrente objeto) {
        return ResponseEntity.ok(service.atualizar(id, objeto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um registro")
    public ResponseEntity<Void> excluir(@PathVariable("id") String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
