package br.com.anna.contaspagarreceber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Exercita a API real, validação, regras de negócio e persistência, sem mocks de repositório. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiEtapa1IntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void limparBancoDeTeste() {
        // Cada requisição usa sua própria transação real; os testes não compartilham cadastros.
        var tabelas = jdbc.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME LIKE 'TB_%'", String.class);
        jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
        try {
            for (String tabela : tabelas) {
                assertTrue(tabela.matches("TB_[A-Z0-9_]+"));
                jdbc.execute("TRUNCATE TABLE " + tabela + " RESTART IDENTITY");
            }
        } finally {
            jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    private LocalDate hoje() {
        return LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo"));
    }

    private ResultActions criar(String rota, ObjectNode corpo) throws Exception {
        return mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(corpo.toString()));
    }

    private JsonNode json(ResultActions resultado) throws Exception {
        return mapper.readTree(resultado.andReturn().getResponse().getContentAsString());
    }

    private ObjectNode conta(String recurso, String titulo, LocalDate vencimento) throws Exception {
        boolean pagar = recurso.equals("pagamentos");
        String cadastro = pagar ? "fornecedores" : "clientes";
        String nome = pagar ? "fornecedor" : "cliente";
        long pessoa = json(criar("/api/" + cadastro, mapper.createObjectNode().put(nome, "Pessoa teste"))
                .andExpect(status().isCreated())).path("registro").asLong();
        long categoria = json(criar("/api/plano-contas", mapper.createObjectNode()
                .put("conta", titulo).put("descricao", "Categoria teste"))
                .andExpect(status().isCreated())).path("planoContas").asLong();
        assertTrue(pessoa > 0);
        assertTrue(categoria > 0);
        return mapper.createObjectNode().put("titulo", titulo)
                .put("emissao", hoje().minusDays(10).toString())
                .put("vencto", vencimento.toString()).put("valor", 150.25)
                .put(pagar ? "fornecedorId" : "clienteId", pessoa)
                .put("categoriaId", categoria).put("descricao", "Conta teste")
                .put("observacoes", "Observação complementar");
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void cadastroConsultaEdicaoEExclusaoDeContaAberta(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode corpo = conta(recurso, "CRUD", hoje().plusDays(5));
        criar(rota, corpo).andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTA"))
                .andExpect(jsonPath("$.descricao").value("Conta teste"))
                .andExpect(jsonPath("$.observacoes").value("Observação complementar"));
        mvc.perform(get(rota + "/CRUD")).andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(150.25));
        corpo.put("descricao", "Conta alterada").put("valor", 200.50);
        mvc.perform(put(rota + "/CRUD").contentType(MediaType.APPLICATION_JSON).content(corpo.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.descricao").value("Conta alterada"));
        mvc.perform(delete(rota + "/CRUD")).andExpect(status().isNoContent());
        mvc.perform(get(rota + "/CRUD")).andExpect(status().isNotFound());
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void duplicidadeNaoSobrescreveCadastroOriginal(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode corpo = conta(recurso, "DUP", hoje());
        criar(rota, corpo).andExpect(status().isCreated());
        corpo.put("valor", 999);
        criar(rota, corpo).andExpect(status().isConflict());
        mvc.perform(get(rota + "/DUP")).andExpect(jsonPath("$.valor").value(150.25));
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void quitacaoIdempotentePreservaDataEFormaEImpedeMudancas(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode corpo = conta(recurso, "QUIT", hoje());
        criar(rota, corpo).andExpect(status().isCreated());
        ObjectNode quitacao = mapper.createObjectNode().put("data", hoje().toString())
                .put("formaPagamento", "PIX");
        for (int i = 0; i < 2; i++) {
            mvc.perform(patch(rota + "/QUIT/quitar").contentType(MediaType.APPLICATION_JSON)
                            .content(quitacao.toString())).andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(recurso.equals("pagamentos") ? "PAGA" : "RECEBIDA"))
                    .andExpect(jsonPath("$.pagto").value(hoje().toString()))
                    .andExpect(jsonPath("$.formaPagamento").value("PIX"));
        }
        quitacao.put("formaPagamento", "DINHEIRO");
        mvc.perform(patch(rota + "/QUIT/quitar").contentType(MediaType.APPLICATION_JSON)
                .content(quitacao.toString())).andExpect(status().isConflict());
        mvc.perform(put(rota + "/QUIT").contentType(MediaType.APPLICATION_JSON).content(corpo.toString()))
                .andExpect(status().isConflict());
        mvc.perform(delete(rota + "/QUIT")).andExpect(status().isConflict());
        mvc.perform(patch(rota + "/QUIT/cancelar")).andExpect(status().isConflict());
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void quitacaoCalculaValorComAcrescimoEDescontoRespeitandoValidade(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode comDesconto = conta(recurso, "DESC", hoje())
                .put("desconto", 10).put("acrescimo", 5).put("validadeDesc", hoje().toString());
        criar(rota, comDesconto).andExpect(status().isCreated());
        ObjectNode quitacao = mapper.createObjectNode().put("data", hoje().toString())
                .put("formaPagamento", "PIX");
        mvc.perform(patch(rota + "/DESC/quitar").contentType(MediaType.APPLICATION_JSON)
                .content(quitacao.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.valorQuitado").value(145.25));
        ObjectNode expirado = conta(recurso, "EXP", hoje())
                .put("desconto", 10).put("acrescimo", 5)
                .put("validadeDesc", hoje().minusDays(1).toString());
        criar(rota, expirado).andExpect(status().isCreated());
        mvc.perform(patch(rota + "/EXP/quitar").contentType(MediaType.APPLICATION_JSON)
                .content(quitacao.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.valorQuitado").value(155.25));
        mvc.perform(get(rota).param("status", recurso.equals("pagamentos") ? "PAGA" : "RECEBIDA"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get(rota).param("status", "ABERTA")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void cancelamentoPreservaHistoricoEImpedeQuitacaoEdicaoEExclusao(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode corpo = conta(recurso, "CANC", hoje());
        criar(rota, corpo).andExpect(status().isCreated());
        mvc.perform(patch(rota + "/CANC/cancelar")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
        mvc.perform(put(rota + "/CANC").contentType(MediaType.APPLICATION_JSON).content(corpo.toString()))
                .andExpect(status().isConflict());
        mvc.perform(delete(rota + "/CANC")).andExpect(status().isConflict());
        mvc.perform(patch(rota + "/CANC/quitar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.createObjectNode().put("data", hoje().toString())
                        .put("formaPagamento", "PIX").toString())).andExpect(status().isConflict());
        mvc.perform(get(rota + "/CANC")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void situacaoDerivadaDoVencimentoEFiltrosComLimitesInclusivos(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        LocalDate ontem = hoje().minusDays(1);
        ObjectNode atrasada = conta(recurso, "ATRASO", ontem).put("status", "ABERTA");
        criar(rota, atrasada).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("VENCIDA"));
        ObjectNode hoje = conta(recurso, "HOJE", hoje()).put("status", "VENCIDA");
        criar(rota, hoje).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("ABERTA"));
        mvc.perform(get(rota + "/ATRASO")).andExpect(jsonPath("$.status").value("VENCIDA"));
        mvc.perform(get(rota).param("status", "VENCIDA").param("inicio", ontem.toString())
                        .param("fim", ontem.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].titulo").value("ATRASO"));
        mvc.perform(get(rota).param("inicio", hoje().toString()).param("fim", hoje().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("HOJE"));
        mvc.perform(get(rota).param("inicio", hoje().toString()).param("fim", ontem.toString()))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void camposObrigatoriosValoresEReferenciasSaoValidados(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode valido = conta(recurso, "VALID", hoje());
        for (String campo : new String[]{"titulo", "emissao", "vencto", "valor", "descricao", "categoriaId",
                recurso.equals("pagamentos") ? "fornecedorId" : "clienteId"}) {
            ObjectNode invalido = valido.deepCopy();
            invalido.remove(campo);
            criar(rota, invalido).andExpect(status().isBadRequest());
        }
        for (double valor : new double[]{0, -1, 1.234}) {
            criar(rota, valido.deepCopy().put("valor", valor)).andExpect(status().isBadRequest());
        }
        criar(rota, valido.deepCopy().put("descricao", "   ")).andExpect(status().isBadRequest());
        criar(rota, valido.deepCopy().put("categoriaId", 999999)).andExpect(status().isBadRequest());
        criar(rota, valido.deepCopy().put(recurso.equals("pagamentos") ? "fornecedorId" : "clienteId", 999999))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void cadastroEEdicaoNaoPodemForjarQuitacaoOuCancelamento(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode valido = conta(recurso, "ESTADO", hoje());
        for (String estado : new String[]{"PAGA", "RECEBIDA", "CANCELADA"}) {
            criar(rota, valido.deepCopy().put("status", estado)).andExpect(status().isBadRequest());
        }
        criar(rota, valido.deepCopy().put("pagto", hoje().toString())).andExpect(status().isBadRequest());
        criar(rota, valido.deepCopy().put("formaPagamento", "PIX")).andExpect(status().isBadRequest());
        criar(rota, valido).andExpect(status().isCreated());
        mvc.perform(put(rota + "/ESTADO").contentType(MediaType.APPLICATION_JSON)
                .content(valido.deepCopy().put("status", recurso.equals("pagamentos") ? "PAGA" : "RECEBIDA").toString()))
                .andExpect(status().isBadRequest());
        mvc.perform(get(rota + "/ESTADO")).andExpect(jsonPath("$.status").value("ABERTA"));
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void datasJsonEnumsInvalidosERecursosAusentesRetornamErrosHttp(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        ObjectNode valido = conta(recurso, "DATA", hoje());
        criar(rota, valido.deepCopy().put("vencto", "2026-02-30")).andExpect(status().isBadRequest());
        criar(rota, valido.deepCopy().put("emissao", hoje().plusDays(1).toString())
                .put("vencto", hoje().plusDays(2).toString())).andExpect(status().isBadRequest());
        criar(rota, valido.deepCopy().put("vencto", hoje().minusDays(11).toString()))
                .andExpect(status().isBadRequest());
        criar(rota, valido.deepCopy().put("status", "INVALIDO")).andExpect(status().isBadRequest());
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content("{invalido"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(rota).param("inicio", "nao-data")).andExpect(status().isBadRequest());
        mvc.perform(get(rota).param("status", "INVALIDO")).andExpect(status().isBadRequest());
        mvc.perform(get(rota + "/INEXISTENTE")).andExpect(status().isNotFound());
        mvc.perform(delete(rota + "/INEXISTENTE")).andExpect(status().isNotFound());
        criar(rota, valido).andExpect(status().isCreated());
        mvc.perform(patch(rota + "/DATA/quitar").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch(rota + "/DATA/quitar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.createObjectNode().put("data", hoje().minusDays(11).toString())
                        .put("formaPagamento", "PIX").toString())).andExpect(status().isBadRequest());
        mvc.perform(patch(rota + "/DATA/quitar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.createObjectNode().put("data", hoje().toString())
                        .put("formaPagamento", "INVALIDA").toString())).andExpect(status().isBadRequest());
    }

    @ParameterizedTest @ValueSource(strings = {"pagamentos", "recebimentos"})
    void categoriasEPessoasReferenciadasNaoPodemSerExcluidas(String recurso) throws Exception {
        ObjectNode corpo = conta(recurso, "REF", hoje());
        criar("/api/" + recurso, corpo).andExpect(status().isCreated());
        mvc.perform(delete("/api/plano-contas/" + corpo.path("categoriaId").asLong()))
                .andExpect(status().isConflict());
        boolean pagar = recurso.equals("pagamentos");
        mvc.perform(delete("/api/" + (pagar ? "fornecedores" : "clientes") + "/"
                + corpo.path(pagar ? "fornecedorId" : "clienteId").asLong())).andExpect(status().isConflict());
    }

    @ParameterizedTest @ValueSource(strings = {"clientes", "fornecedores", "plano-contas"})
    void cadastrosAuxiliaresPossuemCrudCompleto(String recurso) throws Exception {
        String rota = "/api/" + recurso;
        String campo = recurso.equals("clientes") ? "cliente" : recurso.equals("fornecedores") ? "fornecedor" : "conta";
        ObjectNode corpo = mapper.createObjectNode().put(campo, "Cadastro");
        if (recurso.equals("plano-contas")) corpo.put("descricao", "Categoria");
        JsonNode salvo = json(criar(rota, corpo).andExpect(status().isCreated()));
        long id = salvo.path(recurso.equals("plano-contas") ? "planoContas" : "registro").asLong();
        mvc.perform(get(rota + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$." + campo).value("Cadastro"));
        corpo.put(campo, "Alterado");
        mvc.perform(put(rota + "/" + id).contentType(MediaType.APPLICATION_JSON).content(corpo.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$." + campo).value("Alterado"));
        mvc.perform(get(rota)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete(rota + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(rota + "/" + id)).andExpect(status().isNotFound());
        criar(rota, mapper.createObjectNode()).andExpect(status().isBadRequest());
    }

    @Test
    void protocoloHttpPreserva404405E415() throws Exception {
        mvc.perform(get("/api/rota-inexistente")).andExpect(status().isNotFound());
        mvc.perform(put("/api/pagamentos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/api/pagamentos").contentType(MediaType.TEXT_PLAIN).content("texto"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void resumoFinanceiroContaHistoricoEExcluiCanceladasDosValores() throws Exception {
        for (String recurso : new String[]{"pagamentos", "recebimentos"}) {
            String rota = "/api/" + recurso;
            criar(rota, conta(recurso, "ABR-" + recurso, hoje().plusDays(5))).andExpect(status().isCreated());
            criar(rota, conta(recurso, "VEN-" + recurso, hoje().minusDays(1))).andExpect(status().isCreated());
            criar(rota, conta(recurso, "QUI-" + recurso, hoje()).put("desconto", 10).put("acrescimo", 5))
                    .andExpect(status().isCreated());
            criar(rota, conta(recurso, "CAN-" + recurso, hoje())).andExpect(status().isCreated());
            mvc.perform(patch(rota + "/QUI-" + recurso + "/quitar").contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.createObjectNode().put("data", hoje().toString()).put("formaPagamento", "PIX")
                            .toString())).andExpect(status().isOk());
            mvc.perform(patch(rota + "/CAN-" + recurso + "/cancelar")).andExpect(status().isOk());
        }
        mvc.perform(get("/api/relatorios/resumo")).andExpect(status().isOk())
                .andExpect(jsonPath("$.clientes").value(4)).andExpect(jsonPath("$.fornecedores").value(4))
                .andExpect(jsonPath("$.despesas").value(0))
                .andExpect(jsonPath("$.contasAPagar").value(4)).andExpect(jsonPath("$.contasAReceber").value(4))
                .andExpect(jsonPath("$.valorAPagarEmAberto").value(300.50))
                .andExpect(jsonPath("$.valorAReceberEmAberto").value(300.50))
                .andExpect(jsonPath("$.valorPago").value(145.25))
                .andExpect(jsonPath("$.valorRecebido").value(145.25));
    }

    @Test
    void swaggerPublicaOperacoesDaParte1() throws Exception {
        JsonNode documento = json(mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()));
        for (String recurso : new String[]{"pagamentos", "recebimentos"}) {
            assertTrue(documento.path("paths").has("/api/" + recurso));
            assertTrue(documento.path("paths").has("/api/" + recurso + "/{id}/quitar"));
            assertTrue(documento.path("paths").has("/api/" + recurso + "/{id}/cancelar"));
        }
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
