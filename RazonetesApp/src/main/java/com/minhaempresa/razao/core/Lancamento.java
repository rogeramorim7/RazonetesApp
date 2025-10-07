package com.minhaempresa.razao.core;

import java.io.Serializable; // ⭐ NOVO: Import para serialização
import java.math.BigDecimal;
import java.time.LocalDate;

// ⭐ CORREÇÃO: Implementa java.io.Serializable
public class Lancamento implements Serializable { 

    // ⭐ RECOMENDADO: Adicionado serialVersionUID para controle de versão
    private static final long serialVersionUID = 1L;

    // ATRIBUTOS NÃO SÃO MAIS FINAL PARA PERMITIR EDIÇÃO
    private BigDecimal valor;
    private TipoLancamento tipo;
    private String historico;
    private LocalDate data;

    public Lancamento(BigDecimal valor, TipoLancamento tipo, String historico, LocalDate data) {
        // Garantir que o valor seja sempre positivo, o TipoLancamento define o efeito.
        this.valor = valor.abs();
        this.tipo = tipo;
        this.historico = historico;
        this.data = data;
    }

    // --- GETTERS (mantidos) ---
    public BigDecimal getValor() {
        return valor;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public String getHistorico() {
        return historico;
    }

    public LocalDate getData() {
        return data;
    }

    // --- SETTERS ADICIONADOS PARA EDIÇÃO ---
    public void setValor(BigDecimal valor) {
        this.valor = valor.abs();
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("%s | %s: R$ %.2f", data, tipo, valor);
    }
}