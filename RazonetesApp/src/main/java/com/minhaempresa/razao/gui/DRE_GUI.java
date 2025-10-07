package com.minhaempresa.razao.gui;

import com.minhaempresa.razao.core.Razonete;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.math.RoundingMode;

public class DRE_GUI extends JDialog { // Use JDialog para ser modal

    private final Map<String, Razonete> contas;
    private JTextArea dreArea;

    // Estrutura de categorização simplificada para demonstração
    private static final Map<String, String> CATEGORIAS_PARA_DRE = Map.of(
        "RECEITA DE VENDAS", "RECEITA",
        "CUSTO DA MERCADORIA VENDIDA", "CUSTO",
        "DESPESAS COM ALUGUEL", "DESPESA",
        "DESPESAS COM VENDAS", "DESPESA"
        // Adicione outras contas de Resultado aqui
    );
    
    public DRE_GUI(Frame parent, Map<String, Razonete> contas) {
        super(parent, "Demonstração do Resultado do Exercício (DRE)", true);
        this.contas = contas;
        
        setLayout(new BorderLayout());
        dreArea = new JTextArea(20, 40);
        dreArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        dreArea.setEditable(false);
        add(new JScrollPane(dreArea), BorderLayout.CENTER);
        
        calcularEGerarDRE();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void calcularEGerarDRE() {
        BigDecimal receitaBruta = BigDecimal.ZERO;
        BigDecimal custos = BigDecimal.ZERO;
        BigDecimal despesas = BigDecimal.ZERO;

        for (Map.Entry<String, Razonete> entry : contas.entrySet()) {
            String nomeConta = entry.getKey().toUpperCase();
            Razonete conta = entry.getValue();
            
            // O saldo é Débito - Crédito
            BigDecimal saldo = conta.calcularSaldo().setScale(2, RoundingMode.HALF_UP);

            String categoria = CATEGORIAS_PARA_DRE.getOrDefault(nomeConta, "IGNORAR");

            switch (categoria) {
                case "RECEITA":
                    // Receitas têm saldo CREDOR (negativo, pois Crédito > Débito).
                    // Para o cálculo, usamos o valor absoluto (ou invertemos o sinal).
                    receitaBruta = receitaBruta.add(saldo.negate()); 
                    break;
                case "CUSTO":
                    // Custos têm saldo DEVEDOR (positivo).
                    custos = custos.add(saldo);
                    break;
                case "DESPESA":
                    // Despesas têm saldo DEVEDOR (positivo).
                    despesas = despesas.add(saldo);
                    break;
                default:
                    // Ignora contas patrimoniais
                    break;
            }
        }
        
        // CÁLCULOS PRINCIPAIS
        BigDecimal resultadoBruto = receitaBruta.subtract(custos);
        BigDecimal resultadoLiquido = resultadoBruto.subtract(despesas);
        
        // Geração do Relatório (DRE)
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("      DEMONSTRAÇÃO DO RESULTADO DO EXERCÍCIO (DRE)\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("RECEITA BRUTA DE VENDAS:  %20.2f\n", receitaBruta));
        sb.append(String.format("(-) CUSTO DAS VENDAS:    (%20.2f)\n", custos));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("= RESULTADO BRUTO:        %20.2f\n", resultadoBruto));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("(-) DESPESAS OPERACIONAIS: (%20.2f)\n", despesas));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("= RESULTADO LÍQUIDO:      %20.2f\n", resultadoLiquido));
        sb.append("--------------------------------------------------\n");
        
        dreArea.setText(sb.toString());
    }
}