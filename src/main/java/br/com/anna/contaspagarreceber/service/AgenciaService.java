package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.Agencia;
import br.com.anna.contaspagarreceber.repository.AgenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import br.com.anna.contaspagarreceber.repository.BancoRepository;
import br.com.anna.contaspagarreceber.repository.ContaCorrenteRepository;
import br.com.anna.contaspagarreceber.repository.ChequePreRepository;

import java.util.List;

@Service
@Transactional
public class AgenciaService {

    private final AgenciaRepository repository;

    private final BancoRepository bancoRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final ChequePreRepository chequePreRepository;

    public AgenciaService(AgenciaRepository repository, BancoRepository bancoRepository,
                         ContaCorrenteRepository contaCorrenteRepository, ChequePreRepository chequePreRepository) {
        this.repository = repository;
        this.bancoRepository = bancoRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.chequePreRepository = chequePreRepository;

    }

    @Transactional(readOnly = true)
    public List<Agencia> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Agencia buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agencia não encontrado: " + id));
    }

    public Agencia salvar(Agencia objeto) {
        if (repository.existsById(objeto.getAgencia())) {
            throw new ConflitoException("Identificador já cadastrado.");
        }
        validar(objeto);
        return repository.save(objeto);
    }

    public Agencia atualizar(String id, Agencia objeto) {
        Agencia atual = buscarPorId(id);
        if (!java.util.Objects.equals(atual.getBanco(), objeto.getBanco())
                && (contaCorrenteRepository.findAll().stream().anyMatch(conta -> id.equals(conta.getAgencia()))
                || chequePreRepository.findAll().stream().anyMatch(cheque -> id.equals(cheque.getAgencia())))) {
            throw new ConflitoException("Não é possível trocar o banco de uma agência vinculada a contas ou cheques.");
        }
        objeto.setAgencia(id);
        validar(objeto);
        return repository.save(objeto);
    }

    public void excluir(String id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void validar(Agencia objeto) {
        if (!bancoRepository.existsById(objeto.getBanco())) {
            throw new DadosInvalidosException("Banco informado não existe.");
        }
    }
}
