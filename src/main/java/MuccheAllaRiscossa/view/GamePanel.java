package MuccheAllaRiscossa.view;

import MuccheAllaRiscossa.controller.GameController;
import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.view.assets.Sprites;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * GamePanel: schermata principale della partita.
 *
 * Disegna su un {@link Canvas} la griglia di gioco, le unità e i nemici,
 * con un HUD in alto per le risorse. Il game-loop è retto da un
 * {@link AnimationTimer} a ~30 FPS che fa avanzare la logica del
 * {@code GameController} e ridisegna ogni frame.
 */
public class GamePanel extends Pane {

    private static final int LARGHEZZA = 1024;
    private static final int ALTEZZA = 600;

    // Costanti per il disegno della griglia (5 righe x 10 colonne)
    private static final int RIGHE = 5;
    private static final int COLONNE = 10;
    private static final int LARGHEZZA_CELLA = 80;
    private static final int ALTEZZA_CELLA = 80;
    private static final int OFFSET_X = 50;
    private static final int OFFSET_Y = 100;

    /** Intervallo minimo tra un tick logico e l'altro: ~30 FPS. */
    private static final long FRAME_NS = 33_000_000L;

    private final GameWindow window;
    private final Canvas canvas;
    private final GraphicsContext g;
    private final AnimationTimer gameTimer;

    @SuppressWarnings("unused")
    private final String difensoreSelezionato = "V"; // "V" sta per Mucca Standard come esempio iniziale

    public GamePanel(GameWindow window) {
        this.window = window;

        this.canvas = new Canvas(LARGHEZZA, ALTEZZA);
        this.g = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        // Intercettiamo il click del mouse per posizionare i difensori sulla griglia
        canvas.setOnMouseClicked(e -> {
            int mouseX = (int) e.getX() - OFFSET_X;
            int mouseY = (int) e.getY() - OFFSET_Y;

            // Controlliamo se il click è avvenuto all'interno dei confini della griglia
            if (mouseX >= 0 && mouseX < COLONNE * LARGHEZZA_CELLA
                    && mouseY >= 0 && mouseY < RIGHE * ALTEZZA_CELLA) {
                int colonnaCliccata = mouseX / LARGHEZZA_CELLA;
                int rigaCliccata = mouseY / ALTEZZA_CELLA;

                System.out.println("[GUI] Tentativo di piazzamento in Riga: " + rigaCliccata
                        + ", Colonna: " + colonnaCliccata);
                // TODO: Quando le classi dei difensori concreti saranno pronte, istanzieremo
                // la mucca corretta in base a 'difensoreSelezionato' e chiameremo
                // GameController.getInstance().schiera(...)
            }
        });

        // Loop di gioco basato su AnimationTimer, throttle a ~30 FPS (≈ 33 ms per frame).
        this.gameTimer = new AnimationTimer() {
            private long ultimoTick = 0L;

            @Override
            public void handle(long now) {
                if (now - ultimoTick < FRAME_NS) return;
                ultimoTick = now;

                // Se la partita è finita (es. salute stalla a zero), fermiamo il gioco
                if (GameController.getInstance().isGameOver()) {
                    fermaGioco();
                    window.mostraPannello("GAME_OVER");
                    return;
                }

                // Avanzamento logico e refresh grafico
                GameController.getInstance().tick();
                disegna();
            }
        };
    }

    /** Avvia il loop di gioco. */
    public void avviaGioco() {
        gameTimer.start();
    }

    /** Ferma il loop di gioco. */
    public void fermaGioco() {
        gameTimer.stop();
    }

    /** Forza un singolo redraw indipendentemente dal tick (utile per i callback HUD). */
    public void forzaRedraw() {
        disegna();
    }

    private void disegna() {
        // Reset frame
        g.clearRect(0, 0, LARGHEZZA, ALTEZZA);

        // --- 1. DISEGNO HUD IN ALTO ---
        g.setFill(Color.DARKGRAY);
        g.fillRect(0, 0, LARGHEZZA, 65);

        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        g.fillText("Fieno (Balle): " + GameController.getInstance().getBalleDiFieno(), 30, 40);
        g.fillText("Integrità Stalla: " + GameController.getInstance().getStalla().getIntegritaSistema() + "%", 260, 40);
        g.fillText("Ondata Corrente: " + GameController.getInstance().getOndataCorrente(), 550, 40);

        if (GameController.getInstance().isInPausa()) {
            g.setFill(Color.YELLOW);
            g.fillText("[PAUSA PREPARAZIONE]", 780, 40);
        }

        // --- 2. DISEGNO GRIGLIA DI GIOCO ---
        for (int r = 0; r < RIGHE; r++) {
            for (int c = 0; c < COLONNE; c++) {
                Color colore;
                if ((r + c) % 2 == 0) {
                    colore = Color.rgb(50, 205, 50); // Verde lime/prato chiaro
                } else {
                    colore = Color.rgb(34, 139, 34); // Verde foresta/prato scuro
                }

                // La corsia centrale (riga index 2) la facciamo marrone come percorso preferenziale di terra
                if (r == 2) {
                    colore = Color.rgb(139, 69, 19);
                }

                int xCella = OFFSET_X + c * LARGHEZZA_CELLA;
                int yCella = OFFSET_Y + r * ALTEZZA_CELLA;

                g.setFill(colore);
                g.fillRect(xCella, yCella, LARGHEZZA_CELLA, ALTEZZA_CELLA);
                g.setStroke(Color.rgb(0, 0, 0, 0.16)); // Bordo leggermente scuro per staccare le celle
                g.strokeRect(xCella, yCella, LARGHEZZA_CELLA, ALTEZZA_CELLA);
            }
        }

        // --- 3. DISEGNO UNITA BOVINE (DIFENSORI) ---
        for (UnitaBovina mucca : GameController.getInstance().getUnita()) {
            int xMucca = OFFSET_X + mucca.getColonna() * LARGHEZZA_CELLA + 8;
            int yMucca = OFFSET_Y + mucca.getRiga() * ALTEZZA_CELLA + 8;
            Image sprite = Sprites.of(mucca);
            g.drawImage(sprite, xMucca, yMucca, 64, 64);
        }

        // --- 4. DISEGNO INSETTI MUTANTI (NEMICI) ---
        for (InsettoMutante insetto : GameController.getInstance().getInsetti()) {
            int xInsetto = OFFSET_X + (int) (insetto.getColonna() * LARGHEZZA_CELLA) + 8;
            int yInsetto = OFFSET_Y + (insetto.getRiga() * ALTEZZA_CELLA) + 8;
            Image sprite = Sprites.of(insetto);
            g.drawImage(sprite, xInsetto, yInsetto, 64, 64);
        }
    }
}
