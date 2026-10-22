package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_despesa")
public class Despesa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long despesa;

    @Column(name = "lancamento")
    private Long lancamento;

    @NotNull
    private LocalDate dataLanc;

    private LocalDate prevPagto;

    @NotNull @DecimalMin(value="0.0", inclusive = false)
    @Column(precision = 15, scale = 2)
    @Digits(integer = 13, fraction = 2)
    private BigDecimal valorPrevisto;

    @Size(max=80)
    private String descricao;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lancamento", insertable = false, updatable = false)
    private Lancamento lancamentoReferencia;

    public Despesa() {}

    public Long getDespesa() {
        return despesa;
    }

    public void setDespesa(Long despesa) {
        this.despesa = despesa;
    }

    public Long getLancamento() {
        return lancamento;
    }

    public void setLancamento(Long lancamento) {
        this.lancamento = lancamento;
    }

    public LocalDate getDataLanc() {
        return dataLanc;
    }

    public void setDataLanc(LocalDate dataLanc) {
        this.dataLanc = dataLanc;
    }

    public LocalDate getPrevPagto() {
        return prevPagto;
    }

    public void setPrevPagto(LocalDate prevPagto) {
        this.prevPagto = prevPagto;
    }

    public BigDecimal getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(BigDecimal valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
