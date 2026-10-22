package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.Fornecedor;
import br.com.anna.contaspagarreceber.repository.FornecedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import java.util.List;

@Service
@Transactional
public class FornecedorService {

    private final FornecedorRepository repository;

    public FornecedorService(FornecedorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Fornecedor> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Fornecedor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado: " + id));
    }

    public Fornecedor salvar(Fornecedor objeto) {
        if (objeto.getRegistro() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        return repository.save(objeto);
    }

    public Fornecedor atualizar(Long id, Fornecedor objeto) {
        buscarPorId(id);
        objeto.setRegistro(id);
        return repository.save(objeto);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }
}
