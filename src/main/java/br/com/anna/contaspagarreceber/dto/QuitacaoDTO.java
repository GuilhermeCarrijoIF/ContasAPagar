package br.com.anna.contaspagarreceber.dto;

import br.com.anna.contaspagarreceber.enums.FormaPagamento;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class QuitacaoDTO {

    @NotNull
    private LocalDate data;

    @NotNull
    private FormaPagamento formaPagamento;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
