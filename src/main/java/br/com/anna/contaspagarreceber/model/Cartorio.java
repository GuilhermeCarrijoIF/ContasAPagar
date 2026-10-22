package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cartorio")
public class Cartorio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registro;

    @NotBlank @Size(max=30)
    private String nome;

    @Size(max=30)
    private String contato;

    @Size(max=13)
    private String telefone;

    public Cartorio() {}

    public Long getRegistro() {
        return registro;
    }

    public void setRegistro(Long registro) {
        this.registro = registro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

}
