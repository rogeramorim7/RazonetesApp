package com.minhaempresa.razao.gui;

import com.minhaempresa.razao.core.Razonete;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap; // Usamos TreeMap para ordenar as contas por nome

public class BalanceteGUI extends JDialog {

    private final Map<String, Razonete> contas;

    public BalanceteGUI(Frame parent, Map<String, Razonete> contas) {
        super(parent, "Balancete de Verificação", true);
        this.contas = contas;

        // Configuração visual da janela
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        // --- Tabela para o Balancete ---
        String[] cols = {"Conta", "Saldo Devedor (D)", "Saldo Credor (C)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Cálculo e Apresentação ---
        calcularEPopularBalancete(model);

        setVisible(true); // Mostrar a janela após carregar os dados
    }

    private void calcularEPopularBalancete(DefaultTableModel model) {
        // Usamos TreeMap para garantir que as contas sejam ordenadas alfabeticamente
        Map<String, Razonete> contasOrdenadas = new TreeMap<>(contas);
        
        BigDecimal totalDevedor = BigDecimal.ZERO;
        BigDecimal totalCredor = BigDecimal.ZERO;

        for (var entry : contasOrdenadas.entrySet()) {
            String nome = entry.getKey();
            Razonete raz = entry.getValue();

            // O saldo final é a diferença entre Débito e Crédito (D - C)
            BigDecimal saldo = raz.calcularSaldo().setScale(2, RoundingMode.HALF_UP);
            
            BigDecimal saldoDevedor = BigDecimal.ZERO;
            BigDecimal saldoCredor = BigDecimal.ZERO;

            // Se o saldo for maior que zero (positivo), é Devedor
            if (saldo.signum() > 0) {
                saldoDevedor = saldo;
                totalDevedor = totalDevedor.add(saldoDevedor);
            // Se o saldo for menor que zero (negativo), é Credor (mostramos o valor absoluto)
            } else if (saldo.signum() < 0) {
                saldoCredor = saldo.abs(); // Usamos valor absoluto
                totalCredor = totalCredor.add(saldoCredor);
            }
            
            // Adiciona a linha à tabela (usamos Double.class para a tabela exibir valores numéricos)
            model.addRow(new Object[]{
                nome, 
                saldoDevedor.compareTo(BigDecimal.ZERO) == 0 ? null : saldoDevedor, 
                saldoCredor.compareTo(BigDecimal.ZERO) == 0 ? null : saldoCredor
            });
        }
        
        // --- Linha de Totais ---
        model.addRow(new Object[]{"-------------------", "-------------------", "-------------------"});
        model.addRow(new Object[]{"TOTAL GERAL:", totalDevedor, totalCredor});

        // --- Determinar se o Balancete fecha ---
        String status;
        Color cor;
        if (totalDevedor.compareTo(totalCredor) == 0) {
            status = "BALANCETE FECHADO (Débito = Crédito)";
            cor = new Color(0, 150, 0); // Verde escuro
        } else {
            status = "BALANCETE NÃO FECHADO (Diferença: R$ " + totalDevedor.subtract(totalCredor).abs().setScale(2, RoundingMode.HALF_UP) + ")";
            cor = Color.RED;
        }

        JLabel lblStatus = new JLabel(status, SwingConstants.CENTER);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblStatus.setForeground(cor);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Adiciona a label de status ao rodapé da janela
        add(lblStatus, BorderLayout.SOUTH);
    }
}