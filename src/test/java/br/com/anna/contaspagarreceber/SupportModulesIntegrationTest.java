package br.com.anna.contaspagarreceber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;
import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Verifica os cadastros e vínculos do modelo Delphi preservado na Parte 1. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SupportModulesIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void limparBanco() {
        var tabelas = jdbc.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME LIKE 'TB_%'", String.class);
        jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
        try {
            for (String tabela : tabelas) {
                assertTrue(tabela.matches("TB_[A-Z0-9_]+"));
                jdbc.execute("TRUNCATE TABLE " + tabela + " RESTART IDENTITY");
            }
        } finally { jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE"); }
    }

    private LocalDate hoje() { return LocalDate.now(ZoneId.of("America/Sao_Paulo")); }
    private ObjectNode objeto() { return mapper.createObjectNode(); }
    private ResultActions enviar(String recurso, ObjectNode corpo) throws Exception {
        return mvc.perform(post("/api/" + recurso).contentType(MediaType.APPLICATION_JSON).content(corpo.toString()));
    }
    private JsonNode criar(String recurso, ObjectNode corpo) throws Exception {
        return mapper.readTree(enviar(recurso, corpo).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
    }
    private ResultActions editar(String recurso, Object id, ObjectNode corpo) throws Exception {
        return mvc.perform(put("/api/" + recurso + "/" + id).contentType(MediaType.APPLICATION_JSON).content(corpo.toString()));
    }
    private ResultActions excluir(String recurso, Object id) throws Exception {
        return mvc.perform(delete("/api/" + recurso + "/" + id));
    }
    private void redeBancaria() throws Exception {
        criar("bancos", objeto().put("banco", "001").put("nome", "Banco A"));
        criar("bancos", objeto().put("banco", "002").put("nome", "Banco B"));
        criar("agencias", objeto().put("agencia", "A1").put("banco", "001").put("nome", "Agência A"));
        criar("agencias", objeto().put("agencia", "A2").put("banco", "002").put("nome", "Agência B"));
        criar("contas-correntes", contaCorrente());
    }
    private ObjectNode contaCorrente() {
        return objeto().put("contaCorrente", "CC1").put("banco", "001").put("agencia", "A1").put("limite", 500);
    }
    private ObjectNode cheque() {
        return objeto().put("numCheque", "123").put("banco", "001").put("agencia", "A1")
                .put("contaCorrente", "CC1").put("valor", 50.25)
                .put("emissao", hoje().toString()).put("deposito", hoje().plusDays(1).toString());
    }
    private long categoria(String codigo) throws Exception {
        return criar("plano-contas", objeto().put("conta", codigo).put("descricao", "Categoria " + codigo))
                .path("planoContas").asLong();
    }

    @Test
    void bancosEAgenciasPossuemCrudEIdentificadoresNaoSobrescrevemCadastros() throws Exception {
        ObjectNode banco = objeto().put("banco", "001").put("nome", "Banco A");
        criar("bancos", banco);
        enviar("bancos", banco.deepCopy().put("nome", "Outro")).andExpect(status().isConflict());
        editar("bancos", "001", banco.put("nome", "Atualizado")).andExpect(status().isOk());
        mvc.perform(get("/api/bancos/001")).andExpect(jsonPath("$.nome").value("Atualizado"));
        ObjectNode agencia = objeto().put("agencia", "A1").put("banco", "001").put("nome", "Agência");
        criar("agencias", agencia);
        enviar("agencias", agencia).andExpect(status().isConflict());
        editar("agencias", "A1", agencia.put("nome", "Agência alterada")).andExpect(status().isOk());
        excluir("bancos", "001").andExpect(status().isConflict());
        excluir("agencias", "A1").andExpect(status().isNoContent());
        excluir("bancos", "001").andExpect(status().isNoContent());
        mvc.perform(get("/api/bancos/001")).andExpect(status().isNotFound());
        enviar("bancos", objeto().put("banco", "X")).andExpect(status().isBadRequest());
        enviar("agencias", agencia.put("banco", "999")).andExpect(status().isBadRequest());
    }

    @Test
    void contaEChequeExigemVinculosBancariosConsistentesEValoresValidos() throws Exception {
        redeBancaria();
        enviar("contas-correntes", contaCorrente().put("contaCorrente", "CC2").put("banco", "002"))
                .andExpect(status().isBadRequest());
        enviar("contas-correntes", contaCorrente().put("contaCorrente", "CC2").put("agencia", "X"))
                .andExpect(status().isBadRequest());
        enviar("contas-correntes", contaCorrente()).andExpect(status().isConflict());
        enviar("cheques-pre-datados", cheque().put("banco", "002")).andExpect(status().isBadRequest());
        enviar("cheques-pre-datados", cheque().put("contaCorrente", "X")).andExpect(status().isBadRequest());
        enviar("cheques-pre-datados", cheque().put("agencia", "A2").put("banco", "002"))
                .andExpect(status().isBadRequest());
        enviar("cheques-pre-datados", cheque().put("deposito", hoje().minusDays(1).toString()))
                .andExpect(status().isBadRequest());
        for (double valor : new double[]{0, -1, 1.234})
            enviar("cheques-pre-datados", cheque().put("valor", valor)).andExpect(status().isBadRequest());
        enviar("cheques-pre-datados", cheque().put("registro", 123)).andExpect(status().isBadRequest());
    }

    @Test
    void referenciasBancariasProtegemExclusaoETrocaDeBancoOuAgencia() throws Exception {
        redeBancaria();
        long id = criar("cheques-pre-datados", cheque()).path("registro").asLong();
        mvc.perform(get("/api/cheques-pre-datados/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.contaCorrente").value("CC1"));
        mvc.perform(get("/api/cheques-pre-datados")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/contas-correntes/CC1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.agencia").value("A1"));
        mvc.perform(get("/api/agencias/A1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.banco").value("001"));
        editar("agencias", "A1", objeto().put("agencia", "A1").put("banco", "002").put("nome", "Agência"))
                .andExpect(status().isConflict());
        editar("contas-correntes", "CC1", contaCorrente().put("banco", "002").put("agencia", "A2"))
                .andExpect(status().isConflict());
        excluir("contas-correntes", "CC1").andExpect(status().isConflict());
        excluir("agencias", "A1").andExpect(status().isConflict());
        excluir("bancos", "001").andExpect(status().isConflict());
        editar("cheques-pre-datados", id, cheque().put("descricao", "Cheque alterado"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.descricao").value("Cheque alterado"));
        excluir("cheques-pre-datados", id).andExpect(status().isNoContent());
        editar("contas-correntes", "CC1", contaCorrente().put("limite", 700)).andExpect(status().isOk());
        excluir("contas-correntes", "CC1").andExpect(status().isNoContent());
        excluir("agencias", "A1").andExpect(status().isNoContent());
        excluir("bancos", "001").andExpect(status().isNoContent());
    }

    @Test
    void planoDeContasImpedeDuplicidadeCiclosEPaiInexistenteOuExcluido() throws Exception {
        long pai = categoria("1");
        ObjectNode filho = objeto().put("conta", "1.1").put("descricao", "Filho").put("superior", "1");
        long idFilho = criar("plano-contas", filho).path("planoContas").asLong();
        enviar("plano-contas", objeto().put("conta", "1")).andExpect(status().isConflict());
        enviar("plano-contas", objeto().put("conta", "2").put("superior", "X")).andExpect(status().isBadRequest());
        enviar("plano-contas", objeto().put("conta", "2").put("superior", "2")).andExpect(status().isBadRequest());
        editar("plano-contas", pai, objeto().put("conta", "1").put("superior", "1.1"))
                .andExpect(status().isBadRequest());
        excluir("plano-contas", pai).andExpect(status().isConflict());
        editar("plano-contas", pai, objeto().put("conta", "3")).andExpect(status().isConflict());
        excluir("plano-contas", idFilho).andExpect(status().isNoContent());
        excluir("plano-contas", pai).andExpect(status().isNoContent());
    }

    @Test
    void lancamentosEDespesasValidamVinculosDatasEProtegemHistoricoReferenciado() throws Exception {
        long credito = categoria("C");
        long debito = categoria("D");
        ObjectNode lancamento = objeto().put("contaCredito", credito).put("contaDebito", debito)
                .put("data", hoje().toString()).put("valor", 100);
        enviar("lancamentos", lancamento.deepCopy().put("contaDebito", credito)).andExpect(status().isBadRequest());
        enviar("lancamentos", lancamento.deepCopy().put("contaCredito", 999)).andExpect(status().isBadRequest());
        enviar("lancamentos", lancamento.deepCopy().put("valor", 0)).andExpect(status().isBadRequest());
        long idLancamento = criar("lancamentos", lancamento).path("lancamento").asLong();
        ObjectNode despesa = objeto().put("lancamento", idLancamento).put("dataLanc", hoje().toString())
                .put("prevPagto", hoje().plusDays(2).toString()).put("valorPrevisto", 100);
        enviar("despesas", despesa.deepCopy().put("lancamento", 999)).andExpect(status().isBadRequest());
        enviar("despesas", despesa.deepCopy().put("prevPagto", hoje().minusDays(1).toString()))
                .andExpect(status().isBadRequest());
        enviar("despesas", despesa.deepCopy().put("valorPrevisto", 1.234)).andExpect(status().isBadRequest());
        long idDespesa = criar("despesas", despesa).path("despesa").asLong();
        excluir("lancamentos", idLancamento).andExpect(status().isConflict());
        excluir("plano-contas", credito).andExpect(status().isConflict());
        editar("despesas", idDespesa, despesa.put("descricao", "Despesa alterada")).andExpect(status().isOk());
        editar("lancamentos", idLancamento, lancamento.put("descricao", "Lançamento alterado"))
                .andExpect(status().isOk());
        excluir("despesas", idDespesa).andExpect(status().isNoContent());
        excluir("lancamentos", idLancamento).andExpect(status().isNoContent());
        excluir("plano-contas", credito).andExpect(status().isNoContent());
    }

    @Test
    void remessasExigemTitulosECartoriosExistentesEDatasEmOrdem() throws Exception {
        long cliente = criar("clientes", objeto().put("cliente", "Cliente" )).path("registro").asLong();
        long categoria = categoria("R");
        criar("recebimentos", objeto().put("titulo", "R1").put("clienteId", cliente).put("categoriaId", categoria)
                .put("descricao", "Recebimento").put("valor", 100).put("emissao", hoje().toString())
                .put("vencto", hoje().plusDays(10).toString()));
        long cartorio = criar("cartorios", objeto().put("nome", "Cartório")).path("registro").asLong();
        ObjectNode remessa = objeto().put("registro", cartorio).put("titulo", "R1").put("remessa", hoje().toString());
        enviar("remessas-cartorio", remessa.deepCopy().put("registro", 999)).andExpect(status().isBadRequest());
        enviar("remessas-cartorio", remessa.deepCopy().put("titulo", "X")).andExpect(status().isBadRequest());
        enviar("remessas-cartorio", remessa.deepCopy().put("protesto", hoje().minusDays(1).toString()))
                .andExpect(status().isBadRequest());
        enviar("remessas-cartorio", remessa.deepCopy().put("baixa", hoje().minusDays(1).toString()))
                .andExpect(status().isBadRequest());
        enviar("remessas-cartorio", remessa.deepCopy().put("idRemessa", 99)).andExpect(status().isBadRequest());
        long id = criar("remessas-cartorio", remessa).path("idRemessa").asLong();
        excluir("cartorios", cartorio).andExpect(status().isConflict());
        excluir("recebimentos", "R1").andExpect(status().isConflict());
        editar("remessas-cartorio", id, remessa.put("protesto", hoje().toString()).put("baixa", hoje().toString()))
                .andExpect(status().isOk());
        mvc.perform(get("/api/remessas-cartorio/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("R1"));
        excluir("remessas-cartorio", id).andExpect(status().isNoContent());
        excluir("recebimentos", "R1").andExpect(status().isNoContent());
        excluir("cartorios", cartorio).andExpect(status().isNoContent());
    }
}
