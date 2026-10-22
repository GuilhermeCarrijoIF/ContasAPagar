package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.Cliente;
import br.com.anna.contaspagarreceber.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
    }

    public Cliente salvar(Cliente objeto) {
        if (objeto.getRegistro() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        return repository.save(objeto);
    }

    public Cliente atualizar(Long id, Cliente objeto) {
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
