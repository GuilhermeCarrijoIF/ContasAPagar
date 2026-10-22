package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_conta_corrente")
public class ContaCorrente {

    @Id @NotBlank @Size(max=15)
    private String contaCorrente;

    @Size(max=4)
    @Column(name = "banco")
    private String banco;

    @Size(max=10)
    @Column(name = "agencia")
    private String agencia;

    @DecimalMin(value="0.0")
    @Column(precision = 15, scale = 2)
    @Digits(integer = 13, fraction = 2)
    private BigDecimal limite;

    private Integer tipo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banco", insertable = false, updatable = false)
    private Banco bancoReferencia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia", insertable = false, updatable = false)
    private Agencia agenciaReferencia;

    public ContaCorrente() {}

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
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

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

}
