package com.minhaempresa.razao.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para classificar contas e calcular saldos agregados
 * necessários para o Balanço Patrimonial (BP) e Demonstração do Resultado (DRE).
 */
public class ClassificacaoContas {

    public enum GrupoConta {
        ATIVO,
        PASSIVO,
        RECEITA,
        DESPESA,
        PATRIMONIO_LIQUIDO,
        NAO_CLASSIFICADO // Para contas criadas que não se encaixam
    }

    // ⭐ CLASSIFICAÇÃO DE CONTAS CENTRALIZADA
    private static final Map<String, GrupoConta> CLASSIFICACAO_CONTAS = new HashMap<>();

    static {
        // --- ATIVO (Contas com saldo DEVEDOR normal - D-C > 0) ---
        CLASSIFICACAO_CONTAS.put("CAIXA", GrupoConta.ATIVO);
        CLASSIFICACAO_CONTAS.put("BANCOS", GrupoConta.ATIVO);
        CLASSIFICACAO_CONTAS.put("CONTAS A RECEBER", GrupoConta.ATIVO);
        CLASSIFICACAO_CONTAS.put("ESTOQUES", GrupoConta.ATIVO);
        CLASSIFICACAO_CONTAS.put("MÁQUINAS E EQUIPAMENTOS", GrupoConta.ATIVO);
        CLASSIFICACAO_CONTAS.put("VEÍCULOS", GrupoConta.ATIVO);
        CLASSIFICACAO_CONTAS.put("IMÓVEIS", GrupoConta.ATIVO);

        // --- PASSIVO (Contas com saldo CREDOR normal - D-C < 0) ---
        CLASSIFICACAO_CONTAS.put("FORNECEDORES", GrupoConta.PASSIVO);
        CLASSIFICACAO_CONTAS.put("SALÁRIOS A PAGAR", GrupoConta.PASSIVO);
        CLASSIFICACAO_CONTAS.put("IMPOSTOS A RECOLHER", GrupoConta.PASSIVO);
        CLASSIFICACAO_CONTAS.put("EMPRÉSTIMOS A PAGAR", GrupoConta.PASSIVO);

        // --- PATRIMÔNIO LÍQUIDO (PL) (Contas com saldo CREDOR normal - D-C < 0) ---
        CLASSIFICACAO_CONTAS.put("CAPITAL SOCIAL", GrupoConta.PATRIMONIO_LIQUIDO);
        CLASSIFICACAO_CONTAS.put("RESERVAS DE LUCRO", GrupoConta.PATRIMONIO_LIQUIDO);

        // --- RECEITAS (Contas de Resultado CREDORAS - D-C < 0) ---
        CLASSIFICACAO_CONTAS.put("RECEITA DE VENDAS", GrupoConta.RECEITA);
        CLASSIFICACAO_CONTAS.put("RECEITA DE SERVIÇOS", GrupoConta.RECEITA);
        
        // --- DESPESAS (Contas de Resultado DEVEDORAS - D-C > 0) ---
        CLASSIFICACAO_CONTAS.put("CUSTO DA MERCADORIA VENDIDA", GrupoConta.DESPESA);
        CLASSIFICACAO_CONTAS.put("DESPESAS COM ALUGUEL", GrupoConta.DESPESA);
        CLASSIFICACAO_CONTAS.put("DESPESAS COM SALÁRIOS", GrupoConta.DESPESA);
        CLASSIFICACAO_CONTAS.put("DESPESAS COM LUZ E ÁGUA", GrupoConta.DESPESA);
    }
    
    /**
     * Retorna o GrupoConta para um nome de conta.
     * Ignora maiúsculas/minúsculas e espaços em excesso.
     */
    public static GrupoConta getGrupoConta(String nomeConta) {
        // Converte e busca, retornando NÃO_CLASSIFICADO se não encontrar
        return CLASSIFICACAO_CONTAS.getOrDefault(
            nomeConta.toUpperCase().trim(), GrupoConta.NAO_CLASSIFICADO
        );
    }
    
    /**
     * Calcula o saldo total de todas as contas em um grupo específico.
     * @param contas Mapa de todas as contas Razonete.
     * @param grupo O GrupoConta (ex: ATIVO).
     * @return O saldo total ABSOLUTO do grupo.
     */
    public static BigDecimal calcularTotalPorGrupo(Map<String, Razonete> contas, GrupoConta grupo) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<String, Razonete> entry : contas.entrySet()) {
            String nomeConta = entry.getKey();
            Razonete razonete = entry.getValue();
            
            if (getGrupoConta(nomeConta) == grupo) {
                // Saldo = Débito - Crédito
                BigDecimal saldo = razonete.calcularSaldo().setScale(2, RoundingMode.HALF_UP);
                
                // Natureza Devedora (Ativo, Despesa): Saldo POSITIVO (D-C > 0)
                if (grupo == GrupoConta.ATIVO || grupo == GrupoConta.DESPESA) {
                    if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                        total = total.add(saldo); // Soma o valor positivo
                    }
                } 
                // Natureza Credora (Passivo, Receita, PL): Saldo NEGATIVO (D-C < 0)
                else if (grupo == GrupoConta.PASSIVO || grupo == GrupoConta.RECEITA || grupo == GrupoConta.PATRIMONIO_LIQUIDO) {
                    if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                        // Passivo/Receita/PL são credoras, mas o valor do BP deve ser positivo (absoluto)
                        total = total.add(saldo.negate()); 
                    }
                }
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o Lucro (ou Prejuízo) do Exercício: Receitas - Despesas.
     * @param contas Mapa de todas as contas Razonete.
     * @return O saldo do lucro/prejuízo. Positivo é Lucro. Negativo é Prejuízo.
     */
    public static BigDecimal calcularLucroExercicio(Map<String, Razonete> contas) {
        BigDecimal totalReceita = calcularTotalPorGrupo(contas, GrupoConta.RECEITA);
        BigDecimal totalDespesa = calcularTotalPorGrupo(contas, GrupoConta.DESPESA);
        
        // Lucro é a Receita (Credora) - Despesa (Devedora)
        return totalReceita.subtract(totalDespesa).setScale(2, RoundingMode.HALF_UP);
    }
}