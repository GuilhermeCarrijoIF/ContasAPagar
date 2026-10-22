package br.com.anna.contaspagarreceber.service;

import br.com.anna.contaspagarreceber.repository.ClienteRepository;
import br.com.anna.contaspagarreceber.repository.DespesaRepository;
import br.com.anna.contaspagarreceber.repository.FornecedorRepository;
import br.com.anna.contaspagarreceber.repository.PagamentoRepository;
import br.com.anna.contaspagarreceber.repository.RecebimentoRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly=true)
public class RelatorioService {

    private final ClienteRepository clienteRepository;
    private final FornecedorRepository fornecedorRepository;
    private final DespesaRepository despesaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final RecebimentoRepository recebimentoRepository;

    public RelatorioService(
            ClienteRepository clienteRepository,
            FornecedorRepository fornecedorRepository,
            DespesaRepository despesaRepository,
            PagamentoRepository pagamentoRepository,
            RecebimentoRepository recebimentoRepository) {
        this.clienteRepository = clienteRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.despesaRepository = despesaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.recebimentoRepository = recebimentoRepository;
    }

    public Map<String, Object> resumo() {
        Map<String, Object> resumo = new LinkedHashMap<>();
        resumo.put("clientes", clienteRepository.count());
        resumo.put("fornecedores", fornecedorRepository.count());
        resumo.put("despesas", despesaRepository.count());
        resumo.put("contasAPagar", pagamentoRepository.count());
        resumo.put("contasAReceber", recebimentoRepository.count());
        var pagamentos = pagamentoRepository.findAll();
        var recebimentos = recebimentoRepository.findAll();
        resumo.put("valorAPagarEmAberto", pagamentos.stream()
            .filter(c -> c.getStatus() == br.com.anna.contaspagarreceber.enums.StatusConta.ABERTA || c.getStatus() == br.com.anna.contaspagarreceber.enums.StatusConta.VENCIDA)
            .map(c -> c.getValor()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        resumo.put("valorAReceberEmAberto", recebimentos.stream()
            .filter(c -> c.getStatus() == br.com.anna.contaspagarreceber.enums.StatusConta.ABERTA || c.getStatus() == br.com.anna.contaspagarreceber.enums.StatusConta.VENCIDA)
            .map(c -> c.getValor()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        resumo.put("valorPago", pagamentos.stream()
            .filter(c -> c.getStatus() == br.com.anna.contaspagarreceber.enums.StatusConta.PAGA)
            .map(c -> c.getValorQuitado()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        resumo.put("valorRecebido", recebimentos.stream()
            .filter(c -> c.getStatus() == br.com.anna.contaspagarreceber.enums.StatusConta.RECEBIDA)
            .map(c -> c.getValorQuitado()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        return resumo;
    }
}
