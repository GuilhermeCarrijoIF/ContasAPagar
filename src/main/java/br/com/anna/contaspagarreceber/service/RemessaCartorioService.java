package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.exception.RecursoNaoEncontradoException;
import br.com.anna.contaspagarreceber.model.RemessaCartorio;
import br.com.anna.contaspagarreceber.repository.RemessaCartorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anna.contaspagarreceber.exception.ConflitoException;
import br.com.anna.contaspagarreceber.exception.DadosInvalidosException;

import br.com.anna.contaspagarreceber.repository.CartorioRepository;
import br.com.anna.contaspagarreceber.repository.RecebimentoRepository;

import java.util.List;

@Service
@Transactional
public class RemessaCartorioService {

    private final RemessaCartorioRepository repository;

    private final CartorioRepository cartorioRepository;
    private final RecebimentoRepository recebimentoRepository;

    public RemessaCartorioService(RemessaCartorioRepository repository, CartorioRepository cartorioRepository, RecebimentoRepository recebimentoRepository) {
        this.repository = repository;
        this.cartorioRepository = cartorioRepository;
        this.recebimentoRepository = recebimentoRepository;

    }

    @Transactional(readOnly = true)
    public List<RemessaCartorio> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public RemessaCartorio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("RemessaCartorio não encontrado: " + id));
    }

    public RemessaCartorio salvar(RemessaCartorio objeto) {
        if (objeto.getIdRemessa() != null) {
            throw new DadosInvalidosException("Não informe identificador gerado pelo sistema no cadastro.");
        }
        validar(objeto);
        return repository.save(objeto);
    }

    public RemessaCartorio atualizar(Long id, RemessaCartorio objeto) {
        buscarPorId(id);
        objeto.setIdRemessa(id);
        validar(objeto);
        return repository.save(objeto);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
        repository.flush();
    }

    private void validar(RemessaCartorio objeto) {
        if (objeto.getRegistro() != null && !cartorioRepository.existsById(objeto.getRegistro())) {
            throw new DadosInvalidosException("Cartório informado não existe.");
        }
        if (objeto.getTitulo() != null && !recebimentoRepository.existsById(objeto.getTitulo())) {
            throw new DadosInvalidosException("Título informado não existe nas contas a receber.");
        }
        if (objeto.getProtesto() != null && (objeto.getRemessa() == null
                || objeto.getProtesto().isBefore(objeto.getRemessa()))) {
            throw new DadosInvalidosException("Protesto exige remessa e não pode ser anterior a ela.");
        }
        if (objeto.getBaixa() != null && (objeto.getRemessa() == null
                || objeto.getBaixa().isBefore(objeto.getRemessa())
                || (objeto.getProtesto() != null && objeto.getBaixa().isBefore(objeto.getProtesto())))) {
            throw new DadosInvalidosException("Baixa exige remessa e não pode ser anterior à remessa ou protesto.");
        }
    }
}
