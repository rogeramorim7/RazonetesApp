package com.minhaempresa.razao.gui;

import com.minhaempresa.razao.core.Razonete;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class AreGUI extends JDialog {

    private final Map<String, Razonete> contas;
    private DefaultTableModel model;
    private JLabel lblRes; // ⭐ Agora um campo da classe

    public AreGUI(Frame parent, Map<String,Razonete> contas) {
        super(parent, "Apuração do Resultado do Exercício (ARE)", true);
        this.contas = contas; 

        String[] cols = {"Conta","Débito","Crédito"};
        model = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
            // ⭐ NOVO: Garante que as colunas de valor sejam tratadas como String formatada
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 2) {
                    return String.class; 
                }
                return Object.class;
            }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        lblRes = new JLabel("", SwingConstants.CENTER);
        lblRes.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblRes.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(lblRes, BorderLayout.SOUTH);
        
        // ⭐ NOVO: Chamada explícita para o método de cálculo e atualização
        atualizarResultados();

        setSize(600,400); // Aumentei a largura para melhor visualização da moeda
        setLocationRelativeTo(parent);
    }
    
    /**
     * Calcula e atualiza a tabela com os saldos e o resultado do exercício.
     */
    private void atualizarResultados() {
        model.setRowCount(0);

        BigDecimal totalDebitoARE = BigDecimal.ZERO; 
        BigDecimal totalCreditoARE = BigDecimal.ZERO; 
        
        RoundingMode round = RoundingMode.HALF_UP;

        for (var entry : contas.entrySet()) {
            String nome = entry.getKey();
            Razonete raz = entry.getValue();
            
            // Recalcula o saldo do razonete ANTES de usá-lo
            BigDecimal saldo = raz.calcularSaldo().setScale(2, round);
            
            String saldoDevedorStr = "";
            String saldoCredorStr = "";
            
            // Verifica o saldo da conta
            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                // Saldo Devedor
                saldoDevedorStr = String.format("R$ %,.2f", saldo);
                
                // ⭐ Apenas contas NÃO-Patrimoniais (diferentes de CAIXA) entram no cálculo do ARE
                if (!nome.equals("CAIXA")) {
                    totalDebitoARE = totalDebitoARE.add(saldo);
                }
                
            } else if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                // Saldo Credor
                BigDecimal saldoAbs = saldo.abs();
                saldoCredorStr = String.format("R$ %,.2f", saldoAbs);
                
                // ⭐ Apenas contas NÃO-Patrimoniais (diferentes de CAIXA) entram no cálculo do ARE
                if (!nome.equals("CAIXA")) {
                    totalCreditoARE = totalCreditoARE.add(saldoAbs);
                }
            }
            
            // Adiciona todas as contas na tabela
            model.addRow(new Object[]{nome, saldoDevedorStr, saldoCredorStr});
        }
        
        // Linha de apuração (Totais da ARE)
        String totalDebitoStr = String.format("R$ %,.2f", totalDebitoARE.setScale(2, round));
        String totalCreditoStr = String.format("R$ %,.2f", totalCreditoARE.setScale(2, round));
        model.addRow(new Object[]{"TOTAL DE CONTAS DE RESULTADO", totalDebitoStr, totalCreditoStr});

        // Determinar Resultado
        String resultado;
        BigDecimal diferenca = totalCreditoARE.subtract(totalDebitoARE).setScale(2, round);
        
        if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
            resultado = "LUCRO (Receitas > Despesas): R$ " + String.format("%,.2f", diferenca);
        } else if (diferenca.compareTo(BigDecimal.ZERO) < 0) {
            resultado = "PREJUÍZO (Despesas > Receitas): R$ " + String.format("%,.2f", diferenca.abs());
        } else {
            resultado = "RESULTADO NULO";
        }
        
        lblRes.setText("Resultado do Exercício: " + resultado);
    }
}