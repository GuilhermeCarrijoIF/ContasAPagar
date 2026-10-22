package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_plano_contas")
public class PlanoContas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planoContas;

    @NotBlank @Size(max=18)
    @Column(unique = true, nullable = false, length = 18)
    private String conta;

    @Size(max=18)
    @Column(name = "superior")
    private String superior;

    @Size(max=60)
    private String descricao;

    private Boolean consolidacao;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superior", referencedColumnName = "conta", insertable = false, updatable = false)
    private PlanoContas superiorReferencia;

    public PlanoContas() {}

    public Long getPlanoContas() {
        return planoContas;
    }

    public void setPlanoContas(Long planoContas) {
        this.planoContas = planoContas;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getConsolidacao() {
        return consolidacao;
    }

    public void setConsolidacao(Boolean consolidacao) {
        this.consolidacao = consolidacao;
    }

}
