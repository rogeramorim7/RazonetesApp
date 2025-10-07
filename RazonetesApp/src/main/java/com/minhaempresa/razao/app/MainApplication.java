package com.minhaempresa.razao.app;

import com.minhaempresa.razao.gui.RazoneteGUI;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada principal da aplicação.
 * Inicia o processo de criação da interface gráfica (GUI).
 */
public class MainApplication {

    public static void main(String[] args) {
        
        // O Swing exige que a interface seja criada e atualizada
        // na Event Dispatch Thread (EDT) para garantir a segurança da thread.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Instancia e exibe a janela principal da aplicação.
                new RazoneteGUI();
            }
        });
        
        // Nota: Se você usasse JavaFX, a lógica de inicialização seria ligeiramente
        // diferente, chamando o método launch().
    }
}