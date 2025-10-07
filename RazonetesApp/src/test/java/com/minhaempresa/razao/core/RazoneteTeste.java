package com.minhaempresa.razao.core;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.minhaempresa.razao.core.Razonete;
import com.minhaempresa.razao.core.Lancamento;
import com.minhaempresa.razao.core.TipoLancamento;

public class RazoneteTeste {
    public static void main(String[] args) {
        // 1. Cria a Conta Razonete (Exemplo: Conta de Ativo "CAIXA")
        Razonete contaCaixa = new Razonete("CAIXA");

        // Valores de exemplo (usando strings para garantir a precisão do BigDecimal)
        // Para a conta de Ativo (Caixa), o Débito AUMENTA e o Crédito DIMINUI.
        
        // 2. Adiciona Lançamentos (Transações)
        
        // Débito (Aumenta o Caixa - Entrada de dinheiro)
        contaCaixa.adicionarLancamento(new Lancamento(
            new BigDecimal("5000.00"), 
            TipoLancamento.DEBITO, 
            "Depósito inicial", 
            LocalDate.of(2025, 10, 1)));

        // Crédito (Diminui o Caixa - Saída de dinheiro)
        contaCaixa.adicionarLancamento(new Lancamento(
            new BigDecimal("1500.50"), 
            TipoLancamento.CREDITO, 
            "Pagamento de aluguel", 
            LocalDate.of(2025, 10, 5)));

        // Débito (Aumenta o Caixa - Venda à vista)
        contaCaixa.adicionarLancamento(new Lancamento(
            new BigDecimal("250.75"), 
            TipoLancamento.DEBITO, 
            "Recebimento de venda", 
            LocalDate.of(2025, 10, 8)));

        // Crédito (Diminui o Caixa - Pequena Despesa)
        contaCaixa.adicionarLancamento(new Lancamento(
            new BigDecimal("20.25"), 
            TipoLancamento.CREDITO, 
            "Compra de material de escritório", 
            LocalDate.of(2025, 10, 10)));
        
        // 3. Exibe o Razonete
        System.out.println("\n--- RELATÓRIO DO RAZONETE ---");
        // ⭐ CORREÇÃO APLICADA AQUI: O método agora é getRelatorio()
        System.out.println(contaCaixa.getRelatorio());
        
        // 4. Exibe o Saldo Final
        System.out.println("Saldo Final Calculado: R$ " + contaCaixa.calcularSaldo());
        
        /*
         * Resumo dos Cálculos:
         * Total Débito (D) = 5000.00 + 250.75 = 5250.75
         * Total Crédito (C) = 1500.50 + 20.25 = 1520.75
         * * Saldo Final = D - C = 5250.75 - 1520.75 = 3730.00
         */
    }
}