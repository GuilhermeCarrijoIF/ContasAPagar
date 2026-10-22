package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.PlanoContas;
import br.com.anna.contaspagarreceber.repository.PlanoContasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import java.util.List;

@Service
@Transactional
public class PlanoContasService {

    private final PlanoContasRepository repository;

    public PlanoContasService(PlanoContasRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PlanoContas> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PlanoContas buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("PlanoContas não encontrado: " + id));
    }

    public PlanoContas salvar(PlanoContas objeto) {
        if (objeto.getPlanoContas() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        validar(objeto);
        return repository.save(objeto);
    }

    public PlanoContas atualizar(Long id, PlanoContas objeto) {
        buscarPorId(id);
        objeto.setPlanoContas(id);
        validar(objeto);
        return repository.save(objeto);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void validar(PlanoContas objeto) {
        var contas = repository.findAll();
        for (PlanoContas conta : contas) {
            if (conta.getConta().equals(objeto.getConta())
                    && !java.util.Objects.equals(conta.getPlanoContas(), objeto.getPlanoContas())) {
                throw new ConflitoException("Código de categoria já cadastrado.");
            }
        }
        String superior = objeto.getSuperior();
        var visitados = new java.util.HashSet<String>();
        visitados.add(objeto.getConta());
        while (superior != null) {
            if (!visitados.add(superior)) {
                throw new DadosInvalidosException("Plano de contas não pode conter ciclos na hierarquia.");
            }
            String codigo = superior;
            PlanoContas pai = contas.stream().filter(conta -> conta.getConta().equals(codigo)).findFirst()
                    .orElseThrow(() -> new DadosInvalidosException("Categoria superior informada não existe."));
            if (java.util.Objects.equals(pai.getPlanoContas(), objeto.getPlanoContas())) {
                throw new DadosInvalidosException("Categoria não pode ser superior de si mesma.");
            }
            superior = pai.getSuperior();
        }
    }
}
