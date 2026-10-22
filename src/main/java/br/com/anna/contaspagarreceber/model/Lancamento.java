package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_lancamento")
public class Lancamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lancamento;

    @Column(name = "conta_credito")
    private Long contaCredito;

    @Column(name = "conta_debito")
    private Long contaDebito;

    @NotNull @DecimalMin(value="0.0", inclusive = false)
    @Column(precision = 15, scale = 2)
    @Digits(integer = 13, fraction = 2)
    private BigDecimal valor;

    @Size(max=50)
    private String descricao;

    @NotNull
    private LocalDate data;

    @Size(max=1)
    private String status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_credito", insertable = false, updatable = false)
    private PlanoContas contaCreditoReferencia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_debito", insertable = false, updatable = false)
    private PlanoContas contaDebitoReferencia;

    public Lancamento() {}

    public Long getLancamento() {
        return lancamento;
    }

    public void setLancamento(Long lancamento) {
        this.lancamento = lancamento;
    }

    public Long getContaCredito() {
        return contaCredito;
    }

    public void setContaCredito(Long contaCredito) {
        this.contaCredito = contaCredito;
    }

    public Long getContaDebito() {
        return contaDebito;
    }

    public void setContaDebito(Long contaDebito) {
        this.contaDebito = contaDebito;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
