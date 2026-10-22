package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.dto.QuitacaoDTO;
import br.com.anna.contaspagarreceber.enums.StatusConta;
import br.com.anna.contaspagarreceber.exception.*;
import br.com.anna.contaspagarreceber.model.Pagamento;
import br.com.anna.contaspagarreceber.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class PagamentoService {
    private final PagamentoRepository repository;
    private final FornecedorRepository pessoaRepository;
    private final PlanoContasRepository categoriaRepository;

    public PagamentoService(PagamentoRepository repository, FornecedorRepository pessoaRepository,
                       PlanoContasRepository categoriaRepository) {
        this.repository = repository;
        this.pessoaRepository = pessoaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly=true)
    public List<Pagamento> listar(StatusConta status, LocalDate inicio, LocalDate fim) {
        if ((inicio == null) != (fim == null)) throw new DadosInvalidosException("Informe inicio e fim do período.");
        if (inicio != null && fim.isBefore(inicio)) throw new DadosInvalidosException("Período invertido.");
        // A situação vencida é derivada da data, sem gravar dados durante consultas.
        return repository.findAll().stream()
            .filter(c -> status == null || c.getStatus() == status)
            .filter(c -> inicio == null || (!c.getVencto().isBefore(inicio) && !c.getVencto().isAfter(fim)))
            .toList();
    }

    @Transactional(readOnly=true)
    public Pagamento buscarPorId(String id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Conta a pagar não encontrada: " + id));
    }

    private Pagamento bloquear(String id) {
        return repository.buscarComBloqueio(id).orElseThrow(() -> new RecursoNaoEncontradoException("Conta a pagar não encontrada: " + id));
    }

    public Pagamento salvar(Pagamento objeto) {
        if (repository.existsById(objeto.getTitulo())) throw new ConflitoException("Já existe uma conta com esse título.");
        validarCadastro(objeto);
        objeto.setStatus(StatusConta.ABERTA);
        return repository.saveAndFlush(objeto);
    }

    public Pagamento atualizar(String id, Pagamento objeto) {
        Pagamento atual = bloquear(id);
        exigirAberta(atual);
        if (!Objects.equals(id, objeto.getTitulo())) throw new DadosInvalidosException("O título do corpo deve ser igual ao da URL.");
        validarCadastro(objeto);
        org.springframework.beans.BeanUtils.copyProperties(objeto, atual,
            "titulo", "status", "pagto", "formaPagamento", "valorQuitado", "versao", "pessoaReferencia", "categoriaReferencia");
        atual.setStatus(StatusConta.ABERTA);
        return repository.saveAndFlush(atual);
    }

    public Pagamento quitar(String id, QuitacaoDTO dados) {
        Pagamento conta = bloquear(id);
        if (dados == null || dados.getData() == null || dados.getFormaPagamento() == null)
            throw new DadosInvalidosException("Informe data e forma de quitação.");
        if (conta.getStatus() == StatusConta.PAGA) {
            if (Objects.equals(conta.getPagto(), dados.getData()) && conta.getFormaPagamento() == dados.getFormaPagamento()) return conta;
            throw new ConflitoException("Conta já quitada com outros dados.");
        }
        exigirAberta(conta);
        if (dados.getData().isBefore(conta.getEmissao()) || dados.getData().isAfter(hoje()))
            throw new DadosInvalidosException("Quitação deve ocorrer entre a emissão e a data atual.");
        BigDecimal desconto = conta.getDesconto() == null ? BigDecimal.ZERO : conta.getDesconto();
        if (conta.getValidadeDesc() != null && dados.getData().isAfter(conta.getValidadeDesc())) desconto = BigDecimal.ZERO;
        BigDecimal acrescimo = conta.getAcrescimo() == null ? BigDecimal.ZERO : conta.getAcrescimo();
        conta.setValorQuitado(conta.getValor().add(acrescimo).subtract(desconto));
        conta.setPagto(dados.getData());
        conta.setFormaPagamento(dados.getFormaPagamento());
        conta.setStatus(StatusConta.PAGA);
        return repository.saveAndFlush(conta);
    }

    public Pagamento cancelar(String id) {
        Pagamento conta = bloquear(id);
        if (conta.getStatus() == StatusConta.CANCELADA) return conta;
        exigirAberta(conta);
        conta.setStatus(StatusConta.CANCELADA);
        return repository.saveAndFlush(conta);
    }

    public void excluir(String id) {
        Pagamento conta = bloquear(id);
        exigirAberta(conta);
        repository.delete(conta);
        repository.flush();
    }

    private void exigirAberta(Pagamento conta) {
        if (conta.getStatus() != StatusConta.ABERTA && conta.getStatus() != StatusConta.VENCIDA)
            throw new ConflitoException("Somente contas abertas ou vencidas permitem esta operação. Histórico quitado/cancelado é preservado.");
    }

    private void validarCadastro(Pagamento conta) {
        if (conta.getStatus() != null && conta.getStatus() != StatusConta.ABERTA && conta.getStatus() != StatusConta.VENCIDA)
            throw new DadosInvalidosException("Use as operações de quitar/cancelar para alterar a situação.");
        if (conta.getPagto() != null || conta.getFormaPagamento() != null || conta.getValorQuitado() != null)
            throw new DadosInvalidosException("Dados de quitação são definidos exclusivamente pela operação quitar.");
        if (conta.getEmissao() == null || conta.getVencto() == null || conta.getVencto().isBefore(conta.getEmissao()))
            throw new DadosInvalidosException("Vencimento não pode ser anterior à emissão.");
        if (conta.getEmissao().isAfter(hoje())) throw new DadosInvalidosException("Emissão não pode ser futura.");
        if (conta.getValor() == null || conta.getValor().signum() <= 0) throw new DadosInvalidosException("Valor deve ser positivo.");
        BigDecimal desconto = conta.getDesconto() == null ? BigDecimal.ZERO : conta.getDesconto();
        BigDecimal acrescimo = conta.getAcrescimo() == null ? BigDecimal.ZERO : conta.getAcrescimo();
        if (desconto.signum() < 0 || acrescimo.signum() < 0 || desconto.compareTo(conta.getValor()) > 0)
            throw new DadosInvalidosException("Desconto deve estar entre zero e o valor original; acréscimo não pode ser negativo.");
        if (conta.getValor().add(acrescimo).compareTo(new BigDecimal("9999999999999.99")) > 0)
            throw new DadosInvalidosException("Valor com acréscimo excede a precisão monetária suportada.");
        if (conta.getValidadeDesc() != null && conta.getValidadeDesc().isBefore(conta.getEmissao()))
            throw new DadosInvalidosException("Validade do desconto não pode ser anterior à emissão.");
        if (conta.getLancamento() != null && (conta.getLancamento().isBefore(conta.getEmissao()) || conta.getLancamento().isAfter(hoje())))
            throw new DadosInvalidosException("Lançamento deve ocorrer entre emissão e data atual.");
        if (conta.getFornecedorId() == null || conta.getCategoriaId() == null)
            throw new DadosInvalidosException("Informe a pessoa e a categoria financeira.");
        var pessoa = pessoaRepository.findById(conta.getFornecedorId())
            .orElseThrow(() -> new DadosInvalidosException("Fornecedor inexistente."));
        if (!categoriaRepository.existsById(conta.getCategoriaId())) throw new DadosInvalidosException("Categoria inexistente.");
        if (conta.getCgc() != null && !Objects.equals(conta.getCgc(), pessoa.getCgc()))
            throw new DadosInvalidosException("CGC deve corresponder à pessoa vinculada.");
        conta.setCgc(pessoa.getCgc());
    }

    private LocalDate hoje() { return LocalDate.now(ZoneId.of("America/Sao_Paulo")); }
}
