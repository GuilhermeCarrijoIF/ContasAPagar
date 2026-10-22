package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.Banco;
import br.com.anna.contaspagarreceber.repository.BancoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import java.util.List;

@Service
@Transactional
public class BancoService {

    private final BancoRepository repository;

    public BancoService(BancoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Banco> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Banco buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Banco não encontrado: " + id));
    }

    public Banco salvar(Banco objeto) {
        if (repository.existsById(objeto.getBanco())) {
            throw new ConflitoException("Identificador já cadastrado.");
        }
        return repository.save(objeto);
    }

    public Banco atualizar(String id, Banco objeto) {
        buscarPorId(id);
        objeto.setBanco(id);
        return repository.save(objeto);
    }

    public void excluir(String id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }
}
