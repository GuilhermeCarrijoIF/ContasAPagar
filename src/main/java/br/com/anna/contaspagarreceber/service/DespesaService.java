package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.Despesa;
import br.com.anna.contaspagarreceber.repository.DespesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import br.com.anna.contaspagarreceber.repository.LancamentoRepository;

import java.util.List;

@Service
@Transactional
public class DespesaService {

    private final DespesaRepository repository;

    private final LancamentoRepository lancamentoRepository;

    public DespesaService(DespesaRepository repository, LancamentoRepository lancamentoRepository) {
        this.repository = repository;
        this.lancamentoRepository = lancamentoRepository;

    }

    @Transactional(readOnly = true)
    public List<Despesa> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Despesa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Despesa não encontrado: " + id));
    }

    public Despesa salvar(Despesa objeto) {
        if (objeto.getDespesa() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        validar(objeto);
        return repository.save(objeto);
    }

    public Despesa atualizar(Long id, Despesa objeto) {
        buscarPorId(id);
        objeto.setDespesa(id);
        validar(objeto);
        return repository.save(objeto);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void validar(Despesa objeto) {
        if (objeto.getLancamento() != null && !lancamentoRepository.existsById(objeto.getLancamento())) {
            throw new DadosInvalidosException("Lançamento informado não existe.");
        }
        if (objeto.getPrevPagto() != null && objeto.getPrevPagto().isBefore(objeto.getDataLanc())) {
            throw new DadosInvalidosException("Pagamento previsto não pode ser anterior ao lançamento.");
        }
    }
}
