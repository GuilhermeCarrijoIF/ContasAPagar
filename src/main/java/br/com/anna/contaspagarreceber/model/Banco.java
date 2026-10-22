package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_banco")
public class Banco {

    @Id @NotBlank @Size(max=4)
    private String banco;

    @NotBlank @Size(max=30)
    private String nome;

    public Banco() {}

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
