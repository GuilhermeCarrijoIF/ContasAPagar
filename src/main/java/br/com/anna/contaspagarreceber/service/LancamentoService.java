package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.Lancamento;
import br.com.anna.contaspagarreceber.repository.LancamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import br.com.anna.contaspagarreceber.repository.PlanoContasRepository;

import java.util.List;

@Service
@Transactional
public class LancamentoService {

    private final LancamentoRepository repository;

    private final PlanoContasRepository planoContasRepository;

    public LancamentoService(LancamentoRepository repository, PlanoContasRepository planoContasRepository) {
        this.repository = repository;
        this.planoContasRepository = planoContasRepository;

    }

    @Transactional(readOnly = true)
    public List<Lancamento> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Lancamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lancamento não encontrado: " + id));
    }

    public Lancamento salvar(Lancamento objeto) {
        if (objeto.getLancamento() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        validar(objeto);
        return repository.save(objeto);
    }

    public Lancamento atualizar(Long id, Lancamento objeto) {
        buscarPorId(id);
        objeto.setLancamento(id);
        validar(objeto);
        return repository.save(objeto);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void validar(Lancamento objeto) {
        if (objeto.getContaCredito() != null && !planoContasRepository.existsById(objeto.getContaCredito())) {
            throw new DadosInvalidosException("Conta de crédito não existe no plano de contas.");
        }
        if (objeto.getContaDebito() != null && !planoContasRepository.existsById(objeto.getContaDebito())) {
            throw new DadosInvalidosException("Conta de débito não existe no plano de contas.");
        }
        if (objeto.getContaCredito() != null && objeto.getContaCredito().equals(objeto.getContaDebito())) {
            throw new DadosInvalidosException("Contas de crédito e débito devem ser diferentes.");
        }
    }
}
