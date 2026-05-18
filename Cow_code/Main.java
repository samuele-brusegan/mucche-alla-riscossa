package MuccheAllaRiscossa;

import MuccheAllaRiscossa.controller.GameController;
import MuccheAllaRiscossa.view.*;
import javax.swing.SwingUtilities;

/**
 * Entry point del gioco adattato all'infrastruttura grafica Swing.
 * Configura la finestra principale, tutti i pannelli di gioco, lo SlideManager
 * e attiva il binding dei callback per la gestione degli errori fatali/danni.
 */
public final class Main {

    public static void main(String[] args) {
        // Avviamo l'interfaccia grafica nel thread sicuro di Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            // 1. Creazione della finestra principale
            GameWindow window = new GameWindow();

            // 2. Istanziazione dei pannelli di gioco reali
            MenuPanel menuPanel = new MenuPanel(window);
            GamePanel gamePanel = new GamePanel(window);
            GameOverPanel gameOverPanel = new GameOverPanel(window);
            VittoriaPanel vittoriaPanel = new VittoriaPanel(window);

            // 3. Registrazione dei pannelli nel container con il CardLayout
            window.getMainContainer().add(menuPanel, "MENU");
            window.getMainContainer().add(gamePanel, "GAME");
            window.getMainContainer().add(gameOverPanel, "GAME_OVER");
            window.getMainContainer().add(vittoriaPanel, "VITTORIA");

            // 4. Configurazione dello SlideManager globale
            SlideManager.getInstance().setWindow(window);

            // 5. Configurazione dei callback (Runnable) su StallaTorreControllo
            // In questo modo, quando la stalla subisce danni o muore, la view risponde subito
            GameController gc = GameController.getInstance();
            
            gc.getStalla().setOnDannoSubito(() -> {
                // Forza il ridisegno del GamePanel per aggiornare la barra dell'HUD in tempo reale
                gamePanel.repaint();
            });

            gc.getStalla().setOnFatalError(() -> {
                // Ferma il timer del game loop e manda la schermata di Game Over
                gamePanel.fermaGioco();
                SlideManager.getInstance().mostra(SlideManager.Slide.GAME_OVER);
            });

            // 6. Mostriamo il menu iniziale e rendiamo visibile l'applicazione
            SlideManager.getInstance().mostra(SlideManager.Slide.MENU);
            window.setVisible(true);

            // Nota: L'avvio effettivo del loop (gamePanel.avviaGioco()) è gestito 
            // all'interno dei pannelli o al cambio di slide verso la partita reale.
        });
    }
}
