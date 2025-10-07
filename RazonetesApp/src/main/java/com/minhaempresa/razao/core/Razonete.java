package com.minhaempresa.razao.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Razonete { 
    
    private final String nomeConta;
    private final List<Lancamento> lancamentos;

    public Razonete(String nomeConta) {
        this.nomeConta = nomeConta;
        this.lancamentos = new ArrayList<>();
    }

    public String getNomeConta() {
        return nomeConta;
    }

    public List<Lancamento> getLancamentos() {
        return lancamentos;
    }

    /**
     * Ordena a lista de lançamentos por data.
     */
    public void ordenarLancamentosPorData() {
        lancamentos.sort(Comparator.comparing(Lancamento::getData));
    }
    
    /**
     * NOVO: Calcula o saldo da conta (Total Débito - Total Crédito) on demand.
     */
    public BigDecimal calcularSaldo() {
        BigDecimal totalDebito = BigDecimal.ZERO;
        BigDecimal totalCredito = BigDecimal.ZERO;
        
        for (Lancamento l : lancamentos) {
            if (l.getTipo() == TipoLancamento.DEBITO) {
                totalDebito = totalDebito.add(l.getValor());
            } else {
                totalCredito = totalCredito.add(l.getValor());
            }
        }
        return totalDebito.subtract(totalCredito);
    }
    

    /**
     * Adiciona um lançamento à conta e recalcula o saldo.
     */
    public void adicionarLancamento(Lancamento lancamento) {
        if (lancamento == null) return;
        this.lancamentos.add(lancamento);
        ordenarLancamentosPorData(); // Mantém a lista ordenada
    }

    /**
     * Remove um lançamento pelo índice.
     */
    public boolean removerLancamento(int index) {
        if (index >= 0 && index < lancamentos.size()) {
            lancamentos.remove(index);
            return true;
        }
        return false;
    }

    /**
     * CORRIGIDO: Renomeado para 'getRelatorio' para coincidir com RazoneteGUI.java.
     * Gera o relatório (Razonete em T).
     */
    public String getRelatorio() { // ⭐ CORRIGIDO AQUI!
        if (lancamentos.isEmpty()) {
            return "CONTA: " + nomeConta + "\n--------------------------------------------------\nNenhum lançamento registrado.";
        }

        // 1. Coleta Débitos e Créditos
        List<BigDecimal> debitos = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DEBITO)
                .map(Lancamento::getValor)
                .collect(Collectors.toList());

        List<BigDecimal> creditos = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.CREDITO)
                .map(Lancamento::getValor)
                .collect(Collectors.toList());

        BigDecimal totalDebito = debitos.stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalCredito = creditos.stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        int maxLancamentos = Math.max(debitos.size(), creditos.size());

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("CONTA: %s\n", nomeConta));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("| %20s | %20s |\n", "DÉBITO (+)", "CRÉDITO (-)"));
        sb.append("--------------------------------------------------\n");

        // Imprime os valores lado a lado
        for (int i = 0; i < maxLancamentos; i++) {
            // Garante que o formato R$ DD.DD será exibido
            String debitoStr = (i < debitos.size()) ? String.format("R$ %15.2f", debitos.get(i).setScale(2, RoundingMode.HALF_UP)) : String.format("%20s", " ");
            String creditoStr = (i < creditos.size()) ? String.format("R$ %15.2f", creditos.get(i).setScale(2, RoundingMode.HALF_UP)) : String.format("%20s", " ");
            sb.append(String.format("| %s | %s |\n", debitoStr, creditoStr));
        }

        sb.append("--------------------------------------------------\n");
        sb.append(String.format("| Total D: R$ %11.2f | Total C: R$ %11.2f |\n", totalDebito, totalCredito));
        sb.append("--------------------------------------------------\n");

        // Usa o saldo calculado
        BigDecimal saldo = calcularSaldo().setScale(2, RoundingMode.HALF_UP);
        String saldoTipo;
        // Se o saldo for positivo ou zero, é Devedor (Ativo/Despesa). Se for negativo, é Credor (Passivo/Receita).
        if (saldo.compareTo(BigDecimal.ZERO) == 0) {
            saldoTipo = "SALDO ZERO";
        } else {
            saldoTipo = saldo.signum() > 0 ? "DEVEDOR" : "CREDOR";
        }

        sb.append(String.format("SALDO FINAL: R$ %13.2f (%s)\n", saldo.abs(), saldoTipo));
        sb.append("--------------------------------------------------\n");

        return sb.toString();
    }
}