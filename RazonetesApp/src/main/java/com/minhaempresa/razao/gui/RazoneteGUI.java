package com.minhaempresa.razao.gui;

import com.minhaempresa.razao.core.Razonete;
import com.minhaempresa.razao.core.Lancamento;
import com.minhaempresa.razao.core.TipoLancamento;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set; 
import java.util.HashSet; 
import com.minhaempresa.razao.gui.BalanceteGUI;
import com.minhaempresa.razao.gui.AreGUI; 

public class RazoneteGUI extends JFrame implements ActionListener {

    // --- FORMATADOR DE DATA BRASILEIRO ---
    private static final DateTimeFormatter DATE_FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ⭐ NOVO: Contas Patrimoniais que o usuário está proibido de criar manualmente
    private static final Set<String> CONTAS_PATRIMONIAIS_PROIBIDAS = new HashSet<>(Set.of(
        "CAIXA", "BANCOS", "CONTAS A RECEBER", "ESTOQUES", "FORNECEDORES", 
        "SALÁRIOS A PAGAR", "CAPITAL SOCIAL"
    ));

    // --- VARIÁVEIS PARA MÚLTIPLAS CONTAS ---
    private Razonete contaAtual;
    private final Map<String, Razonete> contas = new HashMap<>();

    // Componentes da Interface
    private JTextArea relatorioArea;
    private JTextField nomeContaField;
    private JTextField valorField;
    private JTextField historicoField;
    private JTextField dataField;

    // Botões - ADICIONADO BOTÃO DE BP
    private JButton debitarButton, creditarButton, criarContaButton, deletarButton, ordenarButton, editarButton, balanceteButton, areButton, balancoButton; // ⭐ ADICIONADO balancoButton
    private JComboBox<String> seletorContas;
    private JLabel contaAtualLabel;

    // COMPONENTES DA TABELA
    private JTable lancamentosTable;
    private DefaultTableModel tableModel;

    public RazoneteGUI() {
        super("Simulador de Razonete Contábil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- 1. CONFIGURAR PAINEL DE ENTRADA E SELEÇÃO ---
        JPanel inputPanel = configurarPainelInput();

        // --- 2. CONFIGURAR PAINEL DE BOTÕES DE LANÇAMENTO ---
        JPanel buttonPanel = configurarPainelBotoes();

        // --- 3. CONFIGURAÇÃO DA JTABLE ---
        String[] colunas = {"ID (Linha)", "Data (DD/MM/AAAA)", "Tipo", "Valor", "Histórico"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lancamentosTable = new JTable(tableModel);
        lancamentosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(lancamentosTable);

        // 4. Área de Relatório (Razonete T)
        relatorioArea = new JTextArea(15, 50);
        relatorioArea.setEditable(false);
        relatorioArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(relatorioArea);

        // Label de Confirmação da Conta
        contaAtualLabel = new JLabel("NENHUMA CONTA SELECIONADA");
        contaAtualLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contaAtualLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        contaAtualLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // --- 5. MONTAGEM DO FRAME ---
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.add(inputPanel, BorderLayout.NORTH);
        controlsPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dataPanel.add(contaAtualLabel);
        dataPanel.add(new JLabel("--- Razonete em T ---"));
        dataPanel.add(scrollPane);
        dataPanel.add(Box.createVerticalStrut(15));
        dataPanel.add(new JLabel("--- Histórico Detalhado ---"));
        dataPanel.add(tableScrollPane);

        add(controlsPanel, BorderLayout.NORTH);
        add(dataPanel, BorderLayout.CENTER);

        // Inicializa criando a conta padrão "CAIXA"
        adicionarConta("CAIXA");
        seletorContas.setSelectedItem("CAIXA");

        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Configura e retorna o painel de campos de entrada.
     */
    private JPanel configurarPainelInput() {
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        // Campo Nome da NOVA Conta
        inputPanel.add(new JLabel("Nome da NOVA Conta:"));
        nomeContaField = new JTextField("", 15);
        inputPanel.add(nomeContaField);

        // Seletor de Contas Salvas
        inputPanel.add(new JLabel("Conta Selecionada:"));
        seletorContas = new JComboBox<>();
        seletorContas.addActionListener(this);
        inputPanel.add(seletorContas);

        // Campo Data
        inputPanel.add(new JLabel("Data (DD/MM/AAAA):"));
        dataField = new JTextField(LocalDate.now().format(DATE_FORMATTER_BR), 15);
        inputPanel.add(dataField);

        // Campo Valor
        inputPanel.add(new JLabel("Valor (Ex: 1500.50):"));
        valorField = new JTextField("0.00", 15);
        inputPanel.add(valorField);

        // Campo Histórico
        inputPanel.add(new JLabel("Histórico:"));
        historicoField = new JTextField("", 15);
        inputPanel.add(historicoField);

        // Botão de Criação da Nova Conta
        criarContaButton = new JButton("Criar Nova Conta");
        criarContaButton.addActionListener(this);
        inputPanel.add(criarContaButton);
        inputPanel.add(new JLabel(""));

        return inputPanel;
    }

    /**
     * Configura e retorna o painel de botões de ação.
     */
    private JPanel configurarPainelBotoes() {
        // ⭐ ALTERADO: Usando GridLayout para organizar melhor os 8 botões em 2 linhas
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10)); 
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona um pouco de espaço

        // --- LINHA 1: Lançamentos e Edição ---
        debitarButton = new JButton("Lançar DÉBITO (D+)");
        debitarButton.setBackground(new Color(153, 255, 153));
        debitarButton.addActionListener(this);

        creditarButton = new JButton("Lançar CRÉDITO (C+)");
        creditarButton.setBackground(new Color(255, 153, 153));
        creditarButton.addActionListener(this);

        editarButton = new JButton("EDITAR Lançamento");
        editarButton.setBackground(new Color(255, 255, 153)); // Amarelo claro
        editarButton.addActionListener(this);

        deletarButton = new JButton("DELETAR Lançamento");
        deletarButton.setBackground(new Color(255, 204, 204));
        deletarButton.addActionListener(this);

        // --- LINHA 2: Relatórios e Demonstrações ---
        ordenarButton = new JButton("Ordenar por Data");
        ordenarButton.setBackground(new Color(204, 204, 255));
        ordenarButton.addActionListener(this);

        balanceteButton = new JButton("Balancete de Verificação");
        balanceteButton.setBackground(new Color(173, 216, 230)); // LightBlue
        balanceteButton.addActionListener(this);
        
        // ⭐ NOVO: Botão Balanço Patrimonial
        balancoButton = new JButton("BALANÇO PATRIMONIAL (BP)");
        balancoButton.setBackground(new Color(186, 255, 255)); // Ciano claro
        balancoButton.addActionListener(this);
        
        // Botão de ARE
        areButton = new JButton("DRE (ARE)");
        areButton.setBackground(new Color(255, 223, 186)); // Laranja claro/Pêssego
        areButton.addActionListener(this);


        // Adiciona os botões ao painel em ordem
        buttonPanel.add(debitarButton);
        buttonPanel.add(creditarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(deletarButton);
        
        buttonPanel.add(ordenarButton);
        buttonPanel.add(balanceteButton);
        buttonPanel.add(balancoButton); // ⭐ Adicionado ao painel
        buttonPanel.add(areButton);     // ⭐ Adicionado ao painel

        return buttonPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == seletorContas) {
            String nomeSelecionado = (String) seletorContas.getSelectedItem();
            if (nomeSelecionado != null) {
                this.contaAtual = contas.get(nomeSelecionado);
                atualizarRelatorio();
            }

        } else if (source == criarContaButton) {
            String nome = nomeContaField.getText().trim();
            if (!nome.isEmpty()) {
                adicionarConta(nome);
                if (!CONTAS_PATRIMONIAIS_PROIBIDAS.contains(nome.toUpperCase().trim())) {
                    seletorContas.setSelectedItem(nome.toUpperCase());
                }
                nomeContaField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "O nome da conta não pode ser vazio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } else if (source == deletarButton) {
            deletarLancamento();

        } else if (source == editarButton) {
            editarLancamento();

        } else if (source == ordenarButton) {
            if (this.contaAtual != null) {
                contaAtual.ordenarLancamentosPorData();
                atualizarRelatorio();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma conta para ordenar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        // Tratamento do botão Balancete
        } else if (source == balanceteButton) {
            // Abre o balancete em modo modal
            new BalanceteGUI(this, contas).setVisible(true);

        // ⭐ NOVO: Tratamento do botão Balanço Patrimonial
        } else if (source == balancoButton) {
            // Abre o Balanço Patrimonial em modo modal
            new BalancoPatrimonialGUI(this, contas).setVisible(true);

        // Tratamento do botão ARE (DRE)
        } else if (source == areButton) {
            // Abre a Apuração do Resultado do Exercício em modo modal
            new AreGUI(this, contas).setVisible(true);

        } else if (source == debitarButton || source == creditarButton) {
            lancarMovimento(source == debitarButton ? TipoLancamento.DEBITO : TipoLancamento.CREDITO);
        }
    }

    /**
     * FUNCIONALIDADE: Edita um lançamento selecionado na tabela
     */
    private void editarLancamento() {
        if (this.contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaSelecionada = lancamentosTable.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um lançamento na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Lancamento> lancamentos = contaAtual.getLancamentos();
        if (linhaSelecionada >= lancamentos.size()) {
            JOptionPane.showMessageDialog(this, "Lançamento não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Lancamento lancamentoParaEditar = lancamentos.get(linhaSelecionada);

        // Abre dialog de edição
        EdicaoLancamentoDialog dialog = new EdicaoLancamentoDialog(this, lancamentoParaEditar);
        dialog.setVisible(true);

        // Se o dialog foi confirmado, atualiza o lançamento
        if (dialog.isConfirmado()) {
            try {
                // Atualiza os dados do lançamento usando os setters
                lancamentoParaEditar.setValor(dialog.getValorEditado());
                lancamentoParaEditar.setTipo(dialog.getTipoEditado());
                lancamentoParaEditar.setHistorico(dialog.getHistoricoEditado());
                lancamentoParaEditar.setData(dialog.getDataEditada());

                // Recalcula o saldo da conta
                contaAtual.calcularSaldo();

                // Atualiza a interface
                atualizarRelatorio();

                JOptionPane.showMessageDialog(this, "Lançamento editado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao editar lançamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Tenta adicionar um lançamento com o tipo especificado.
     */
    private void lancarMovimento(TipoLancamento tipo) {
        if (this.contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Por favor, crie ou selecione uma conta primeiro.");
            return;
        }

        try {
            String valorTexto = valorField.getText().replace(",", ".");
            BigDecimal valor = new BigDecimal(valorTexto).setScale(2, RoundingMode.HALF_UP);
            String historico = historicoField.getText();
            LocalDate dataLancamento;

            try {
                dataLancamento = LocalDate.parse(dataField.getText().trim(), DATE_FORMATTER_BR);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Erro: Formato de data inválido. Use DD/MM/AAAA.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor deve ser positivo.");
            }

            Lancamento novoLancamento = new Lancamento(valor, tipo, historico, dataLancamento);
            contaAtual.adicionarLancamento(novoLancamento);

            valorField.setText("0.00");
            historicoField.setText("");
            dataField.setText(LocalDate.now().format(DATE_FORMATTER_BR));

            atualizarRelatorio();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Erro: Insira um valor numérico válido (ex: 1500.50).", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Lançamento", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarLancamento() {
        if (this.contaAtual == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma conta.");
            return;
        }

        int linhaSelecionada = lancamentosTable.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um lançamento na tabela para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja deletar o lançamento na linha " + (linhaSelecionada + 1) + "?\nEsta ação é irreversível.",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                contaAtual.removerLancamento(linhaSelecionada);
                atualizarRelatorio();
                JOptionPane.showMessageDialog(this, "Lançamento deletado com sucesso.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar lançamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Adiciona uma nova conta após validar se o nome não é proibido.
     */
    private void adicionarConta(String nome) {
        String nomeFormatado = nome.toUpperCase().trim();
        
        // ⭐ BLOQUEIO E MENSAGEM: Alterado de ERROR_MESSAGE para WARNING_MESSAGE e o texto foi ajustado
        if (CONTAS_PATRIMONIAIS_PROIBIDAS.contains(nomeFormatado)) {
            JLabel errorLabel = new JLabel("<html><b><font color='red'>AVISO: </font></b>A conta '" + nomeFormatado + 
                                         "' é uma conta Patrimonial e não deve ser criada ou duplicada manualmente. Ela já existe ou deve ser criada por apuração.</html>");
            // Tipo de mensagem alterado para WARNING_MESSAGE
            JOptionPane.showMessageDialog(this, errorLabel, "AVISO: Conta Patrimonial Proibida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!contas.containsKey(nomeFormatado)) {
            Razonete novaConta = new Razonete(nomeFormatado);
            contas.put(nomeFormatado, novaConta);
            seletorContas.addItem(nomeFormatado);
        }

        this.contaAtual = contas.get(nomeFormatado);
        atualizarRelatorio();
    }

    private void atualizarRelatorio() {
        if (contaAtual != null) {
            relatorioArea.setText(contaAtual.getRelatorio()); // ⭐ CORRIGIDO: Agora chama getRelatorio()
            contaAtualLabel.setText("CONTA ATIVA: " + contaAtual.getNomeConta());
            atualizarTabela();
        } else {
            relatorioArea.setText("Nenhuma conta selecionada.");
            contaAtualLabel.setText("NENHUMA CONTA SELECIONADA");
        }
    }

    /**
     * Preenche a JTable com os lançamentos da conta atual.
     */
    private void atualizarTabela() {
        tableModel.setRowCount(0);
        if (contaAtual == null) return;

        List<Lancamento> lancamentos = contaAtual.getLancamentos();
        for (int i = 0; i < lancamentos.size(); i++) {
            Lancamento l = lancamentos.get(i);
            String dataStr = l.getData().format(DATE_FORMATTER_BR);
            String tipoStr = l.getTipo().toString();
            String valorStr = String.format("R$ %.2f", l.getValor());
            tableModel.addRow(new Object[]{i + 1, dataStr, tipoStr, valorStr, l.getHistorico()});
        }
    }

    /**
     * Método principal para iniciar a aplicação GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RazoneteGUI());
    }
}

/**
 * CLASSE: Dialog para edição de lançamentos (MANTIDA INTACTA)
 */
class EdicaoLancamentoDialog extends JDialog implements ActionListener {
    private static final DateTimeFormatter DATE_FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private JTextField valorField;
    private JTextField historicoField;
    private JTextField dataField;
    private JComboBox<TipoLancamento> tipoCombo;
    private JButton btnSalvar;
    private JButton btnCancelar;
    
    private boolean confirmado = false;
    private BigDecimal valorEditado;
    private TipoLancamento tipoEditado;
    private String historicoEditado;
    private LocalDate dataEditada;

    public EdicaoLancamentoDialog(Frame parent, Lancamento lancamento) {
        super(parent, "Editar Lançamento", true);
        
        // Configura o dialog
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(parent);

        // Painel principal com campos
        JPanel painelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campo Data
        painelCampos.add(new JLabel("Data (DD/MM/AAAA):"));
        dataField = new JTextField(lancamento.getData().format(DATE_FORMATTER_BR));
        painelCampos.add(dataField);

        // Campo Tipo
        painelCampos.add(new JLabel("Tipo:"));
        tipoCombo = new JComboBox<>(TipoLancamento.values());
        tipoCombo.setSelectedItem(lancamento.getTipo());
        painelCampos.add(tipoCombo);

        // Campo Valor
        painelCampos.add(new JLabel("Valor:"));
        valorField = new JTextField(String.format("%.2f", lancamento.getValor()));
        painelCampos.add(valorField);

        // Campo Histórico
        painelCampos.add(new JLabel("Histórico:"));
        historicoField = new JTextField(lancamento.getHistorico());
        painelCampos.add(historicoField);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        // ActionListeners
        btnSalvar.addActionListener(this);
        btnCancelar.addActionListener(this);

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        // Montagem final
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    // Implementação do ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSalvar) {
            salvarEdicao();
        } else if (e.getSource() == btnCancelar) {
            cancelarEdicao();
        }
    }

    private void salvarEdicao() {
        try {
            // Validação e conversão dos dados
            String valorTexto = valorField.getText().replace(",", ".");
            valorEditado = new BigDecimal(valorTexto).setScale(2, RoundingMode.HALF_UP);
            
            if (valorEditado.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor deve ser positivo.");
            }

            tipoEditado = (TipoLancamento) tipoCombo.getSelectedItem();
            historicoEditado = historicoField.getText().trim();
            
            try {
                dataEditada = LocalDate.parse(dataField.getText().trim(), DATE_FORMATTER_BR);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Formato de data inválido. Use DD/MM/AAAA.");
            }

            confirmado = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Erro: Insira um valor numérico válido (ex: 1500.50).", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarEdicao() {
        confirmado = false;
        dispose();
    }

    // Getters para acessar os dados editados
    public boolean isConfirmado() {
        return confirmado;
    }

    public BigDecimal getValorEditado() {
        return valorEditado;
    }

    public TipoLancamento getTipoEditado() {
        return tipoEditado;
    }

    public String getHistoricoEditado() {
        return historicoEditado;
    }

    public LocalDate getDataEditada() {
        return dataEditada;
    }
}