package br.com.anna.contaspagarreceber.controller;

import br.com.anna.contaspagarreceber.model.Cartorio;
import br.com.anna.contaspagarreceber.service.CartorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartorios")
@Tag(name = "Cartorio", description = "Operações de Cartorio")
public class CartorioController {

    private final CartorioService service;

    public CartorioController(CartorioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos os registros")
    public ResponseEntity<List<Cartorio>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro pelo identificador")
    public ResponseEntity<Cartorio> buscar(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo registro")
    public ResponseEntity<Cartorio> salvar(@Valid @RequestBody Cartorio objeto) {
        return ResponseEntity.status(201).body(service.salvar(objeto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um registro")
    public ResponseEntity<Cartorio> atualizar(@PathVariable("id") Long id, @Valid @RequestBody Cartorio objeto) {
        return ResponseEntity.ok(service.atualizar(id, objeto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um registro")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
