package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.Cartorio;
import br.com.anna.contaspagarreceber.repository.CartorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import java.util.List;

@Service
@Transactional
public class CartorioService {

    private final CartorioRepository repository;

    public CartorioService(CartorioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Cartorio> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cartorio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cartorio não encontrado: " + id));
    }

    public Cartorio salvar(Cartorio objeto) {
        if (objeto.getRegistro() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        return repository.save(objeto);
    }

    public Cartorio atualizar(Long id, Cartorio objeto) {
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
