package com.minhaempresa.razao.core;
public enum TipoLancamento {
    DEBITO,
    CREDITO;

    /**
     * Retorna o multiplicador: 1 para Débito, -1 para Crédito.
     * Isso ajuda a calcular o saldo final (Débito - Crédito).
     */
    public int getMultiplicadorParaCalculo() {
        return (this == DEBITO) ? 1 : -1;
    }
}