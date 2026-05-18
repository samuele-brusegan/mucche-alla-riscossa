package MuccheAllaRiscossa;

import MuccheAllaRiscossa.controller.GameController;
import MuccheAllaRiscossa.view.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Entry point del gioco basato su JavaFX.
 *
 * Configura la finestra principale ({@link GameWindow}), tutti i pannelli
 * di gioco, lo {@link SlideManager} globale e collega i callback della
 * stalla per aggiornare l'HUD/mandare al Game Over in tempo reale.
 */
public final class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // 1. Creazione della finestra principale
        GameWindow window = new GameWindow(stage);

        // 2. Istanziazione dei pannelli di gioco reali
        MenuPanel menuPanel = new MenuPanel(window);
        GamePanel gamePanel = new GamePanel(window);
        GameOverPanel gameOverPanel = new GameOverPanel(window);
        VittoriaPanel vittoriaPanel = new VittoriaPanel(window);

        // 3. Registrazione dei pannelli nel container
        window.aggiungiPannello("MENU", menuPanel);
        window.aggiungiPannello("GAME", gamePanel);
        window.aggiungiPannello("GAME_OVER", gameOverPanel);
        window.aggiungiPannello("VITTORIA", vittoriaPanel);
        window.setGamePanel(gamePanel);

        // 4. Configurazione dello SlideManager globale
        SlideManager.getInstance().setWindow(window);

        // 5. Configurazione dei callback (Runnable) su StallaTorreControllo.
        //    I callback possono arrivare da thread diversi: rimbalziamo sul JavaFX
        //    Application Thread con Platform.runLater per sicurezza.
        GameController gc = GameController.getInstance();

        gc.getStalla().setOnDannoSubito(() -> Platform.runLater(gamePanel::forzaRedraw));

        gc.getStalla().setOnFatalError(() -> Platform.runLater(() -> {
            gamePanel.fermaGioco();
            SlideManager.getInstance().mostra(SlideManager.Slide.GAME_OVER);
        }));

        // 6. Mostriamo il menu iniziale e rendiamo visibile l'applicazione
        SlideManager.getInstance().mostra(SlideManager.Slide.MENU);
        stage.show();

        // Nota: l'avvio effettivo del loop avviene quando entriamo in "GAME"
        // (vedi GameWindow.mostraPannello → gamePanel.avviaGioco()).
    }
}
