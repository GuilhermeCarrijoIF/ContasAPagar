package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.ContaCorrente;
import br.com.anna.contaspagarreceber.repository.ContaCorrenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import br.com.anna.contaspagarreceber.repository.BancoRepository;
import br.com.anna.contaspagarreceber.repository.AgenciaRepository;
import br.com.anna.contaspagarreceber.repository.ChequePreRepository;

import java.util.List;

@Service
@Transactional
public class ContaCorrenteService {

    private final ContaCorrenteRepository repository;

    private final BancoRepository bancoRepository;
    private final AgenciaRepository agenciaRepository;
    private final ChequePreRepository chequePreRepository;

    public ContaCorrenteService(ContaCorrenteRepository repository, BancoRepository bancoRepository,
                               AgenciaRepository agenciaRepository, ChequePreRepository chequePreRepository) {
        this.repository = repository;
        this.bancoRepository = bancoRepository;
        this.agenciaRepository = agenciaRepository;
        this.chequePreRepository = chequePreRepository;

    }

    @Transactional(readOnly = true)
    public List<ContaCorrente> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ContaCorrente buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("ContaCorrente não encontrado: " + id));
    }

    public ContaCorrente salvar(ContaCorrente objeto) {
        if (repository.existsById(objeto.getContaCorrente())) {
            throw new ConflitoException("Identificador já cadastrado.");
        }
        validar(objeto);
        return repository.save(objeto);
    }

    public ContaCorrente atualizar(String id, ContaCorrente objeto) {
        ContaCorrente atual = buscarPorId(id);
        if ((!java.util.Objects.equals(atual.getBanco(), objeto.getBanco())
                || !java.util.Objects.equals(atual.getAgencia(), objeto.getAgencia()))
                && chequePreRepository.findAll().stream().anyMatch(cheque -> id.equals(cheque.getContaCorrente()))) {
            throw new ConflitoException("Não é possível trocar banco ou agência de uma conta vinculada a cheques.");
        }
        objeto.setContaCorrente(id);
        validar(objeto);
        return repository.save(objeto);
    }

    public void excluir(String id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void validar(ContaCorrente objeto) {
        if (objeto.getBanco() != null && !bancoRepository.existsById(objeto.getBanco())) {
            throw new DadosInvalidosException("Banco informado não existe.");
        }
        if (objeto.getAgencia() != null) {
            var agencia = agenciaRepository.findById(objeto.getAgencia())
                    .orElseThrow(() -> new DadosInvalidosException("Agência informada não existe."));
            if (!java.util.Objects.equals(agencia.getBanco(), objeto.getBanco())) {
                throw new DadosInvalidosException("A agência deve pertencer ao banco da conta corrente.");
            }
        }
    }
}
