package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.ChequePre;
import br.com.anna.contaspagarreceber.repository.ChequePreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import br.com.anna.contaspagarreceber.repository.BancoRepository;
import br.com.anna.contaspagarreceber.repository.AgenciaRepository;
import br.com.anna.contaspagarreceber.repository.ContaCorrenteRepository;

import java.util.List;

@Service
@Transactional
public class ChequePreService {

    private final ChequePreRepository repository;

    private final BancoRepository bancoRepository;
    private final AgenciaRepository agenciaRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;

    public ChequePreService(ChequePreRepository repository, BancoRepository bancoRepository, AgenciaRepository agenciaRepository, ContaCorrenteRepository contaCorrenteRepository) {
        this.repository = repository;
        this.bancoRepository = bancoRepository;
        this.agenciaRepository = agenciaRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;

    }

    @Transactional(readOnly = true)
    public List<ChequePre> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ChequePre buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("ChequePre não encontrado: " + id));
    }

    public ChequePre salvar(ChequePre objeto) {
        if (objeto.getRegistro() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        validar(objeto);
        return repository.save(objeto);
    }

    public ChequePre atualizar(Long id, ChequePre objeto) {
        buscarPorId(id);
        objeto.setRegistro(id);
        validar(objeto);
        return repository.save(objeto);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void validar(ChequePre objeto) {
        if (objeto.getBanco() != null && !bancoRepository.existsById(objeto.getBanco())) {
            throw new DadosInvalidosException("Banco informado não existe.");
        }
        if (objeto.getAgencia() != null) {
            var agencia = agenciaRepository.findById(objeto.getAgencia())
                    .orElseThrow(() -> new DadosInvalidosException("Agência informada não existe."));
            if (!java.util.Objects.equals(agencia.getBanco(), objeto.getBanco())) {
                throw new DadosInvalidosException("A agência deve pertencer ao banco do cheque.");
            }
        }
        if (objeto.getContaCorrente() != null) {
            var conta = contaCorrenteRepository.findById(objeto.getContaCorrente())
                    .orElseThrow(() -> new DadosInvalidosException("Conta corrente informada não existe."));
            if (!java.util.Objects.equals(conta.getBanco(), objeto.getBanco())
                    || !java.util.Objects.equals(conta.getAgencia(), objeto.getAgencia())) {
                throw new DadosInvalidosException("Banco e agência devem corresponder à conta corrente do cheque.");
            }
        }
        if (objeto.getEmissao() != null && objeto.getDeposito() != null
                && objeto.getDeposito().isBefore(objeto.getEmissao())) {
            throw new DadosInvalidosException("Depósito não pode ser anterior à emissão do cheque.");
        }
    }
}
