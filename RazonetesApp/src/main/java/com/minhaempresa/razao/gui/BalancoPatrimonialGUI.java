package com.minhaempresa.razao.gui;

import com.minhaempresa.razao.core.Razonete;
import com.minhaempresa.razao.core.Lancamento;
import com.minhaempresa.razao.core.ClassificacaoContas;
import com.minhaempresa.razao.core.ClassificacaoContas.GrupoConta;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.AbstractMap; // Import necessário

public class BalancoPatrimonialGUI extends JDialog {

    private final Map<String, Razonete> contas;
    private final JTextArea bpArea;
    
    private static final DateTimeFormatter DATE_FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int LARGURA_COLUNA = 30; // Aumentado para melhor formatação

    public BalancoPatrimonialGUI(Frame parent, Map<String, Razonete> contas) {
        super(parent, "Balanço Patrimonial", true);
        this.contas = contas;
        
        // Configuração da janela
        setLayout(new BorderLayout());
        setSize(800, 650);
        setLocationRelativeTo(parent);

        bpArea = new JTextArea(30, 80);
        bpArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        bpArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(bpArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Chamada principal
        gerarBalancoPatrimonial();
    }
    
    /**
     * Gera o relatório formatado do Balanço Patrimonial.
     * A lógica de cálculo foi movida para ClassificacaoContas.
     */
    private void gerarBalancoPatrimonial() {
        if (contas.isEmpty()) {
            bpArea.setText("Nenhuma conta registrada para gerar o Balanço Patrimonial.");
            return;
        }
        
        // --- CÁLCULOS PRINCIPAIS USANDO CLASSIFICACAOCONTAS ---
        // 1. Apuração do Lucro/Prejuízo (DRE)
        BigDecimal lucroExercicio = ClassificacaoContas.calcularLucroExercicio(contas);
        
        // 2. Totais Patrimoniais
        BigDecimal totalAtivo = ClassificacaoContas.calcularTotalPorGrupo(contas, GrupoConta.ATIVO);
        BigDecimal totalPassivo = ClassificacaoContas.calcularTotalPorGrupo(contas, GrupoConta.PASSIVO);
        BigDecimal totalPLInicial = ClassificacaoContas.calcularTotalPorGrupo(contas, GrupoConta.PATRIMONIO_LIQUIDO);
        
        // 3. PL Final = PL Inicial + Lucro (ou - Prejuízo)
        BigDecimal totalPL = totalPLInicial.add(lucroExercicio);
        
        // 4. Total do Lado Direito
        BigDecimal totalPassivoPL = totalPassivo.add(totalPL);

        // Data de Referência
        LocalDate dataReferencia = getDataUltimoLancamento();
        
        // --- MONTAGEM DO RELATÓRIO ---
        StringBuilder sb = new StringBuilder();
        String linhaDivisora = "---------------------------------------------------------------------------------\n";
        String linhaTotal = "=================================================================================\n";
        
        // CABEÇALHO
        sb.append(linhaDivisora);
        sb.append(String.format(" BALANÇO PATRIMONIAL %" + (LARGURA_COLUNA - 10) + "s | EM: %s\n", 
            " ", dataReferencia.format(DATE_FORMATTER_BR)));
        sb.append(linhaDivisora);
        
        // TÍTULOS DE COLUNA
        sb.append(String.format("| %-" + LARGURA_COLUNA + "s | %-" + LARGURA_COLUNA + "s | %10s |\n", 
            "ATIVO", "PASSIVO E PL", "VALOR"));
        sb.append(String.format("| %-" + LARGURA_COLUNA + "s | %-" + LARGURA_COLUNA + "s | %10s |\n", 
            "--- CONTAS ---", "--- CONTAS ---", "--- (R$) ---"));
        sb.append(linhaDivisora);

        // Imprime os detalhes do Ativo, Passivo e PL
        imprimirDetalhes(sb, lucroExercicio);
        
        sb.append(linhaDivisora);

        // RODAPÉ E TOTAIS
        String totalAtivoStr = String.format("R$ %" + (LARGURA_COLUNA - 5) + ".2f", totalAtivo);
        String totalPassivoPLStr = String.format("R$ %" + (LARGURA_COLUNA - 5) + ".2f", totalPassivoPL);
        
        sb.append(String.format("| TOTAL ATIVO: %-" + (LARGURA_COLUNA - 5) + "s | TOTAL PASSIVO + PL: %-" + (LARGURA_COLUNA - 5) + "s |\n", 
            totalAtivoStr, totalPassivoPLStr));
        sb.append(linhaTotal);
        
        // VERIFICAÇÃO DA EQUAÇÃO CONTÁBIL
        if (totalAtivo.compareTo(totalPassivoPL) == 0) {
            sb.append("\n✅ A Equação Fundamental da Contabilidade (Ativo = Passivo + PL) está BALANCEADA.");
        } else {
            BigDecimal diferenca = totalAtivo.subtract(totalPassivoPL).abs().setScale(2, RoundingMode.HALF_UP);
            sb.append(String.format("\n❌ DESBALANCEADO. Diferença: R$ %.2f", diferenca));
            sb.append("\nVerifique se todas as contas patrimoniais (Ativo, Passivo, PL) foram classificadas corretamente.");
        }
        
        bpArea.setText(sb.toString());
    }
    
    /**
     * Monta as linhas de detalhes para Ativo, Passivo e Patrimônio Líquido.
     */
    private void imprimirDetalhes(StringBuilder sb, BigDecimal lucroExercicio) {
        
        // Coleta e ordena as contas de ATIVO, PASSIVO e PL
        Map<String, BigDecimal> ativos = new TreeMap<>();
        Map<String, BigDecimal> passivos = new TreeMap<>();
        Map<String, BigDecimal> plContas = new TreeMap<>();
        
        for (Map.Entry<String, Razonete> entry : contas.entrySet()) {
            String nome = entry.getKey();
            GrupoConta grupo = ClassificacaoContas.getGrupoConta(nome);
            BigDecimal saldo = entry.getValue().calcularSaldo().setScale(2, RoundingMode.HALF_UP);
            
            // Ignora contas de resultado ou contas não classificadas que não estão zeradas.
            if (grupo == GrupoConta.RECEITA || grupo == GrupoConta.DESPESA || grupo == GrupoConta.NAO_CLASSIFICADO) {
                continue;
            }
            
            if (saldo.compareTo(BigDecimal.ZERO) == 0) continue;

            if (grupo == GrupoConta.ATIVO && saldo.compareTo(BigDecimal.ZERO) > 0) {
                ativos.put(nome, saldo);
            } else if (grupo == GrupoConta.PASSIVO && saldo.compareTo(BigDecimal.ZERO) < 0) {
                passivos.put(nome, saldo.negate());
            } else if (grupo == GrupoConta.PATRIMONIO_LIQUIDO && saldo.compareTo(BigDecimal.ZERO) < 0) {
                plContas.put(nome, saldo.negate());
            }
        }
        
        // Monta a lista do lado direito (Passivo + PL)
        List<AbstractMap.SimpleEntry<String, BigDecimal>> listaPassivoPL = new ArrayList<>();
        
        // 1. PASSIVO
        listaPassivoPL.add(new AbstractMap.SimpleEntry<>("PASSIVO", BigDecimal.ZERO));
        passivos.forEach((nome, saldo) -> listaPassivoPL.add(new AbstractMap.SimpleEntry<>("   " + nome, saldo)));
        
        // 2. PATRIMÔNIO LÍQUIDO (PL)
        listaPassivoPL.add(new AbstractMap.SimpleEntry<>("PATRIMÔNIO LÍQUIDO", BigDecimal.ZERO));
        plContas.forEach((nome, saldo) -> listaPassivoPL.add(new AbstractMap.SimpleEntry<>("   " + nome, saldo)));
        
        // 3. LUCRO / PREJUÍZO (Resultado do Exercício)
        String nomeResultado = (lucroExercicio.compareTo(BigDecimal.ZERO) >= 0) ? "   LUCRO DO EXERCÍCIO" : "   PREJUÍZO DO EXERCÍCIO";
        listaPassivoPL.add(new AbstractMap.SimpleEntry<>(nomeResultado, lucroExercicio.abs()));
        

        // Imprime as linhas, alinhando Ativo e Passivo/PL
        List<Map.Entry<String, BigDecimal>> listaAtivo = new ArrayList<>(ativos.entrySet());
        int maxLinhas = Math.max(listaAtivo.size(), listaPassivoPL.size());

        for (int i = 0; i < maxLinhas; i++) {
            
            // --- Lado Esquerdo (Ativo) ---
            String ativoNome = "";
            String ativoValorStr = String.format("%" + 10 + "s", " "); 
            
            if (i < listaAtivo.size()) {
                Map.Entry<String, BigDecimal> item = listaAtivo.get(i);
                ativoNome = item.getKey();
                ativoValorStr = String.format("R$ %7.2f", item.getValue());
            }
            
            // Ajuste para o padding do nome
            ativoNome = String.format("%-" + (LARGURA_COLUNA - 10) + "s", ativoNome);
            
            // --- Lado Direito (Passivo + PL) ---
            String passivoPLNome = "";
            String passivoPLValorStr = String.format("%" + 10 + "s", " "); 
            
            if (i < listaPassivoPL.size()) {
                AbstractMap.SimpleEntry<String, BigDecimal> item = listaPassivoPL.get(i);
                passivoPLNome = item.getKey();
                
                // Só imprime o valor se não for um cabeçalho de seção (PASSIVO, PL)
                if (item.getValue().compareTo(BigDecimal.ZERO) != 0 || item.getKey().contains("LUCRO")) {
                    passivoPLValorStr = String.format("R$ %7.2f", item.getValue());
                } else {
                     passivoPLValorStr = String.format("%" + 10 + "s", " ");
                }
            }
            
            // Ajuste para o padding do nome
            passivoPLNome = String.format("%-" + (LARGURA_COLUNA - 10) + "s", passivoPLNome);

            sb.append(String.format("| %s %s | %s %s | %10s |\n", 
                ativoNome, ativoValorStr, passivoPLNome, passivoPLValorStr, " "
            ));
        }
    }
    
    /**
     * Encontra a data do lançamento mais recente em todas as contas.
     */
    private LocalDate getDataUltimoLancamento() {
        return contas.values().stream()
                .flatMap(r -> r.getLancamentos().stream())
                .max(Comparator.comparing(Lancamento::getData))
                .map(Lancamento::getData)
                .orElse(LocalDate.now());
    }
    
    // Classe auxiliar para o map (MANTIDA, pois é usada internamente na formatação)
    private static class AbstractMap {
        static class SimpleEntry<K, V> implements Map.Entry<K, V> {
            private final K key;
            private V value;

            public SimpleEntry(K key, V value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public K getKey() { return key; }
            @Override
            public V getValue() { return value; }
            @Override
            public V setValue(V value) {
                V oldValue = this.value;
                this.value = value;
                return oldValue;
            }
        }
    }
}