package MuccheAllaRiscossa.view;

import java.util.function.Supplier;

import MuccheAllaRiscossa.controller.GameController;
import MuccheAllaRiscossa.model.difensori.Mucca;
import MuccheAllaRiscossa.model.difensori.MuccaCornuta;
import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.difensori.Vacca;
import MuccheAllaRiscossa.model.difensori.VitellinoVedeo;
import MuccheAllaRiscossa.model.gameplay.BoassaEsplosiva;
import MuccheAllaRiscossa.model.gameplay.Proiettile;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.view.assets.Sprites;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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

    /** Factory della mucca attualmente selezionata dall'HUD. */
    private Supplier<UnitaBovina> difensoreSelezionato = VitellinoVedeo::new;

    public GamePanel(GameWindow window) {
        this.window = window;

        this.canvas = new Canvas(LARGHEZZA, ALTEZZA);
        this.g = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        // Toolbar di selezione difensore (sopra la griglia, sotto la fascia HUD)
        creaToolbarSelezione();

        // Intercettiamo il click del mouse per posizionare i difensori sulla griglia
        canvas.setOnMouseClicked(e -> {
            int mouseX = (int) e.getX() - OFFSET_X;
            int mouseY = (int) e.getY() - OFFSET_Y;

            // Controlliamo se il click è avvenuto all'interno dei confini della griglia
            if (mouseX >= 0 && mouseX < COLONNE * LARGHEZZA_CELLA
                    && mouseY >= 0 && mouseY < RIGHE * ALTEZZA_CELLA) {
                int colonnaCliccata = mouseX / LARGHEZZA_CELLA;
                int rigaCliccata = mouseY / ALTEZZA_CELLA;

                UnitaBovina nuova = difensoreSelezionato.get();
                boolean ok = GameController.getInstance().schiera(nuova, rigaCliccata, colonnaCliccata);
                if (!ok) {
                    System.out.println("[GUI] Piazzamento rifiutato (fieno insufficiente?) costo "
                            + nuova.getCostoFieno() + ", fieno " + GameController.getInstance().getBalleDiFieno());
                } else {
                    System.out.println("[GUI] Piazzato " + nuova.getClass().getSimpleName()
                            + " in (" + rigaCliccata + "," + colonnaCliccata + ")");
                }
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

                // Vittoria: tutte le ondate respinte
                if (GameController.getInstance().isVittoria()) {
                    fermaGioco();
                    window.mostraPannello("VITTORIA");
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

    /**
     * Disegna la progress bar in alto a destra: mostra la quota di insetti
     * completati nella fase corrente. Cambia colore e label se siamo in Grande Orda.
     */
    private void disegnaProgressBar() {
        GameController gc = GameController.getInstance();
        double progresso = gc.getProgressoFase();
        boolean orda = gc.isModalitaGrandeOrda();
        boolean pausa = gc.isInPausa();

        int barX = 760, barY = 18, barW = 230, barH = 22;
        // sfondo barra
        g.setFill(Color.rgb(40, 40, 40));
        g.fillRect(barX, barY, barW, barH);
        // riempimento
        Color fill = orda ? Color.rgb(220, 60, 60) : Color.rgb(120, 200, 90);
        g.setFill(fill);
        g.fillRect(barX + 1, barY + 1, (barW - 2) * progresso, barH - 2);
        // bordo
        g.setStroke(Color.WHITE);
        g.strokeRect(barX, barY, barW, barH);
        // testo sopra la barra
        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        String label;
        if (pausa) {
            label = orda ? "GRANDE ORDA IN ARRIVO!" : "PAUSA — prossima ondata";
        } else {
            int cur = gc.getOndataCorrente();
            int max = MuccheAllaRiscossa.model.gameplay.OndataConfig.ONDATA_FINALE;
            label = orda ? ("GRANDE ORDA dopo W" + cur)
                         : ("ONDATA " + cur + " / " + max);
        }
        g.fillText(label, barX, barY - 4);
    }

    /**
     * Crea la toolbar dei 4 difensori (sotto la fascia HUD, sopra la griglia).
     * Un {@link ToggleGroup} garantisce che ne sia selezionato sempre uno solo;
     * la selezione aggiorna la {@link Supplier factory} usata al click.
     */
    private void creaToolbarSelezione() {
        ToggleGroup gruppo = new ToggleGroup();
        ToggleButton b1 = creaBottoneDifensore("Vitellino (50)", VitellinoVedeo::new, gruppo, 50);
        ToggleButton b2 = creaBottoneDifensore("Beatrice (75)",  Mucca::new,          gruppo, 200);
        ToggleButton b3 = creaBottoneDifensore("Cornuta (125)",  MuccaCornuta::new,   gruppo, 350);
        ToggleButton b4 = creaBottoneDifensore("Vacca (200)",    Vacca::new,          gruppo, 500);
        b1.setSelected(true);
        getChildren().addAll(b1, b2, b3, b4);
    }

    private ToggleButton creaBottoneDifensore(String testo, Supplier<UnitaBovina> factory,
                                               ToggleGroup gruppo, double x) {
        ToggleButton b = new ToggleButton(testo);
        b.setToggleGroup(gruppo);
        b.setLayoutX(x);
        b.setLayoutY(70);
        b.setPrefWidth(140);
        b.setOnAction(e -> {
            if (b.isSelected()) difensoreSelezionato = factory;
            else b.setSelected(true); // garantisce che uno sia sempre attivo
        });
        return b;
    }

    private void disegna() {
        // Reset frame
        g.clearRect(0, 0, LARGHEZZA, ALTEZZA);

        // --- 1. DISEGNO HUD IN ALTO ---
        g.setFill(Color.DARKGRAY);
        g.fillRect(0, 0, LARGHEZZA, 65);

        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        // Icona balla di fieno accanto al contatore
        g.drawImage(Sprites.ballaFieno(32, 32), 20, 18, 32, 32);
        g.fillText("" + GameController.getInstance().getBalleDiFieno(), 58, 40);
        g.fillText("Integrità Stalla: " + GameController.getInstance().getStalla().getIntegritaSistema() + "%", 260, 40);
        g.fillText("Ondata Corrente: " + GameController.getInstance().getOndataCorrente(), 550, 40);

        // Progress bar in alto a destra: avanzamento della fase corrente.
        // Verde per ondate regolari, rosso lampeggiante per la Grande Orda.
        disegnaProgressBar();

        // --- 2. DISEGNO GRIGLIA DI GIOCO ---
        Image erba = Sprites.erba(LARGHEZZA_CELLA, ALTEZZA_CELLA);
        for (int r = 0; r < RIGHE; r++) {
            for (int c = 0; c < COLONNE; c++) {
                int xCella = OFFSET_X + c * LARGHEZZA_CELLA;
                int yCella = OFFSET_Y + r * ALTEZZA_CELLA;
                // Sfondo: tile d'erba ripetuto. Ombreggio leggermente le celle pari
                // per dare comunque il pattern "a scacchiera" senza nascondere l'erba.
                g.drawImage(erba, xCella, yCella, LARGHEZZA_CELLA, ALTEZZA_CELLA);
                if ((r + c) % 2 == 0) {
                    g.setFill(Color.rgb(0, 0, 0, 0.10));
                    g.fillRect(xCella, yCella, LARGHEZZA_CELLA, ALTEZZA_CELLA);
                }
                g.setStroke(Color.rgb(0, 0, 0, 0.16));
                g.strokeRect(xCella, yCella, LARGHEZZA_CELLA, ALTEZZA_CELLA);
            }
        }

        // --- 2b. STALLA disegnata su tutta l'altezza del campo, all'estrema destra ---
        int stallaX = OFFSET_X + COLONNE * LARGHEZZA_CELLA;
        int stallaH = RIGHE * ALTEZZA_CELLA;
        g.drawImage(Sprites.stalla(120, stallaH), stallaX - 20, OFFSET_Y, 120, stallaH);

        // --- 3. DISEGNO UNITA BOVINE (DIFENSORI) ---
        for (UnitaBovina mucca : GameController.getInstance().getUnita()) {
            int xMucca = OFFSET_X + mucca.getColonna() * LARGHEZZA_CELLA + 8;
            int yMucca = OFFSET_Y + mucca.getRiga() * ALTEZZA_CELLA + 8;
            Image sprite = Sprites.of(mucca);
            g.drawImage(sprite, xMucca, yMucca, 64, 64);
        }

        // --- 3b. DISEGNO BOASSE (sotto gli insetti) ---
        Image spriteBoassa = Sprites.boassa();
        for (BoassaEsplosiva b : GameController.getInstance().getTrappole()) {
            int bx = OFFSET_X + b.getColonna() * LARGHEZZA_CELLA + 12;
            int by = OFFSET_Y + b.getRiga() * ALTEZZA_CELLA + 40;
            g.drawImage(spriteBoassa, bx, by, 56, 36);
        }

        // --- 4. DISEGNO INSETTI MUTANTI (NEMICI) ---
        for (InsettoMutante insetto : GameController.getInstance().getInsetti()) {
            int xInsetto = OFFSET_X + (int) (insetto.getColonna() * LARGHEZZA_CELLA) + 8;
            int yInsetto = OFFSET_Y + (insetto.getRiga() * ALTEZZA_CELLA) + 8;
            Image sprite = Sprites.of(insetto);
            g.drawImage(sprite, xInsetto, yInsetto, 64, 64);
        }

        // --- 5. DISEGNO PROIETTILI (vitello/zampa lanciati verso sx) ---
        Image spriteProiettile = Sprites.proiettile();
        for (Proiettile p : GameController.getInstance().getProiettili()) {
            int dim = p.isColpisceTutti() ? 36 : 22; // vitello della Vacca piu' grande
            int px = OFFSET_X + (int) (p.getColonna() * LARGHEZZA_CELLA) + (LARGHEZZA_CELLA - dim) / 2;
            int py = OFFSET_Y + p.getRiga() * ALTEZZA_CELLA + (ALTEZZA_CELLA - dim) / 2;
            g.drawImage(spriteProiettile, px, py, dim, dim);
        }
    }
}
