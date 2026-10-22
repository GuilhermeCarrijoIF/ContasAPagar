package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cheque_pre")
public class ChequePre {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registro;

    @NotBlank @Size(max=10)
    private String numCheque;

    @Size(max=18)
    private String cpfCgc;

    @Size(max=4)
    @Column(name = "banco")
    private String banco;

    @Size(max=10)
    @Column(name = "agencia")
    private String agencia;

    @Size(max=15)
    @Column(name = "conta_corrente")
    private String contaCorrente;

    private LocalDate emissao;

    private LocalDate lancamento;

    private LocalDate deposito;

    @NotNull @DecimalMin(value="0.0", inclusive = false)
    @Column(precision = 15, scale = 2)
    @Digits(integer = 13, fraction = 2)
    private BigDecimal valor;

    @Size(max=50)
    private String descricao;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banco", insertable = false, updatable = false)
    private Banco bancoReferencia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia", insertable = false, updatable = false)
    private Agencia agenciaReferencia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_corrente", insertable = false, updatable = false)
    private ContaCorrente contaCorrenteReferencia;

    public ChequePre() {}

    public Long getRegistro() {
        return registro;
    }

    public void setRegistro(Long registro) {
        this.registro = registro;
    }

    public String getNumCheque() {
        return numCheque;
    }

    public void setNumCheque(String numCheque) {
        this.numCheque = numCheque;
    }

    public String getCpfCgc() {
        return cpfCgc;
    }

    public void setCpfCgc(String cpfCgc) {
        this.cpfCgc = cpfCgc;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public LocalDate getEmissao() {
        return emissao;
    }

    public void setEmissao(LocalDate emissao) {
        this.emissao = emissao;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public void setLancamento(LocalDate lancamento) {
        this.lancamento = lancamento;
    }

    public LocalDate getDeposito() {
        return deposito;
    }

    public void setDeposito(LocalDate deposito) {
        this.deposito = deposito;
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

}
