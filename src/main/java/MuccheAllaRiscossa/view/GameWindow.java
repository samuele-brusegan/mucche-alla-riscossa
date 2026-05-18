package MuccheAllaRiscossa.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * GameWindow: la finestra principale di gioco basata su {@link Stage} JavaFX.
 *
 * Tiene una mappa di pannelli identificati per nome (es. "MENU", "GAME",
 * "GAME_OVER", "VITTORIA") e li scambia come root della {@link Scene},
 * rimpiazzando il CardLayout della versione Swing.
 */
public class GameWindow {

    private static final int LARGHEZZA = 1024;
    private static final int ALTEZZA = 600;

    private final Stage stage;
    private final Scene scene;
    private final Map<String, Parent> pannelli = new HashMap<>();

    /** Riferimento opzionale al GamePanel per avviare/fermare il loop allo switch. */
    private GamePanel gamePanel;

    public GameWindow(Stage stage) {
        this.stage = stage;
        stage.setTitle("Mucche alla Riscossa!");
        stage.setResizable(false);

        // Root iniziale vuoto: viene rimpiazzato non appena viene mostrato un pannello.
        this.scene = new Scene(new StackPane(), LARGHEZZA, ALTEZZA);
        stage.setScene(scene);
    }

    /** Aggiunge un pannello con un nome identificativo. */
    public void aggiungiPannello(String nome, Parent pannello) {
        pannelli.put(nome, pannello);
    }

    /** Collega il GamePanel così da poter avviare/fermare il loop allo switch. */
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /** Sostituisce il pannello attualmente visibile con quello richiesto. */
    public void mostraPannello(String nomePannello) {
        Parent p = pannelli.get(nomePannello);
        if (p == null) {
            System.err.println("[GameWindow] pannello sconosciuto: " + nomePannello);
            return;
        }
        scene.setRoot(p);

        // Avvio/stop automatico del game loop quando entriamo/usciamo dalla schermata di gioco.
        if (gamePanel != null) {
            if ("GAME".equals(nomePannello)) {
                gamePanel.avviaGioco();
            } else {
                gamePanel.fermaGioco();
            }
        }
    }

    public Stage getStage() { return stage; }
}
