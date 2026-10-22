package br.com.anna.contaspagarreceber.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_remessa_cartorio")
public class RemessaCartorio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRemessa;

    @Column(name = "registro")
    private Long registro;

    @Size(max=20)
    @Column(name = "titulo")
    private String titulo;

    private LocalDate remessa;

    private LocalDate protesto;

    private LocalDate baixa;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registro", insertable = false, updatable = false)
    private Cartorio cartorioReferencia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titulo", insertable = false, updatable = false)
    private Recebimento recebimentoReferencia;

    public RemessaCartorio() {}

    public Long getIdRemessa() {
        return idRemessa;
    }

    public void setIdRemessa(Long idRemessa) {
        this.idRemessa = idRemessa;
    }

    public Long getRegistro() {
        return registro;
    }

    public void setRegistro(Long registro) {
        this.registro = registro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getRemessa() {
        return remessa;
    }

    public void setRemessa(LocalDate remessa) {
        this.remessa = remessa;
    }

    public LocalDate getProtesto() {
        return protesto;
    }

    public void setProtesto(LocalDate protesto) {
        this.protesto = protesto;
    }

    public LocalDate getBaixa() {
        return baixa;
    }

    public void setBaixa(LocalDate baixa) {
        this.baixa = baixa;
    }

}
