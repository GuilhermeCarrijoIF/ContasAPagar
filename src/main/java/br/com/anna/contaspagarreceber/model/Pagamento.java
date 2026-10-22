package br.com.anna.contaspagarreceber.model;

import br.com.anna.contaspagarreceber.enums.FormaPagamento;
import br.com.anna.contaspagarreceber.enums.StatusConta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_pagamento")
public class Pagamento {

    @Id @NotBlank @Size(max=20)
    private String titulo;

    @Size(max=18)
    private String cgc;

    @NotNull @DecimalMin(value="0.0", inclusive=false)
    @Digits(integer=13, fraction=2)
    @Column(precision=15, scale=2)
    private BigDecimal valor;

    @NotNull
    private LocalDate emissao;

    @NotNull
    private LocalDate vencto;

    private LocalDate lancamento;

    @DecimalMin(value="0.0")
    @Digits(integer=13, fraction=2)
    @Column(precision=15, scale=2)
    private BigDecimal desconto;

    private LocalDate validadeDesc;

    private LocalDate pagto;

    @DecimalMin(value="0.0")
    @Digits(integer=13, fraction=2)
    @Column(precision=15, scale=2)
    private BigDecimal acrescimo;

    @Size(max=16)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private StatusConta status;

    @NotNull
    @Column(name="fornecedor_id", nullable=false)
    private Long fornecedorId;

    @NotNull
    @Column(name="categoria_id", nullable=false)
    private Long categoriaId;

    @NotBlank @Size(max=255)
    @Column(nullable=false, length=255)
    private String descricao;

    @Size(max=2000)
    @Column(length=2000)
    private String observacoes;

    @Digits(integer=13, fraction=2)
    @Column(precision=15, scale=2)
    private BigDecimal valorQuitado;

    @Version
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long versao;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fornecedor_id", insertable=false, updatable=false, nullable=false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Fornecedor pessoaReferencia;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="categoria_id", insertable=false, updatable=false, nullable=false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private PlanoContas categoriaReferencia;


    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    public Pagamento() {}

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCgc() {
        return cgc;
    }

    public void setCgc(String cgc) {
        this.cgc = cgc;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getEmissao() {
        return emissao;
    }

    public void setEmissao(LocalDate emissao) {
        this.emissao = emissao;
    }

    public LocalDate getVencto() {
        return vencto;
    }

    public void setVencto(LocalDate vencto) {
        this.vencto = vencto;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public void setLancamento(LocalDate lancamento) {
        this.lancamento = lancamento;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public LocalDate getValidadeDesc() {
        return validadeDesc;
    }

    public void setValidadeDesc(LocalDate validadeDesc) {
        this.validadeDesc = validadeDesc;
    }

    public LocalDate getPagto() {
        return pagto;
    }

    public void setPagto(LocalDate pagto) {
        this.pagto = pagto;
    }

    public BigDecimal getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(BigDecimal acrescimo) {
        this.acrescimo = acrescimo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("status")
    public StatusConta getStatus() {
        if ((status == StatusConta.ABERTA || status == StatusConta.VENCIDA) && vencto != null) {
            return vencto.isBefore(LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo")))
                    ? StatusConta.VENCIDA : StatusConta.ABERTA;
        }
        return status;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("status")
    public void setStatus(StatusConta status) {
        this.status = status;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }


    public Long getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(Long valor) { this.fornecedorId = valor; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long valor) { this.categoriaId = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String valor) { this.descricao = valor; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String valor) { this.observacoes = valor; }

    public BigDecimal getValorQuitado() { return valorQuitado; }
    public void setValorQuitado(BigDecimal valor) { this.valorQuitado = valor; }
}
