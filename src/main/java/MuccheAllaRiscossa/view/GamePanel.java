package MuccheAllaRiscossa.view;

import MuccheAllaRiscossa.controller.GameController;
import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.view.assets.Sprites;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * GamePanel: Schermata principale della partita.
 * Gestisce il rendering 2D della griglia di gioco, delle unità e dei nemici,
 * l'HUD in alto con le risorse e un Timer Swing per il loop a ~30 FPS.
 */
public class GamePanel extends JPanel {
    
    private final GameWindow window;
    private final Timer gameTimer;
    private final String difensoreSelezionato = "V"; // "V" sta per Mucca Standard come esempio iniziale

    // Costanti per il disegno della griglia (5 righe x 10 colonne)
    private final int RIGHE = 5;
    private final int COLONNE = 10;
    private final int LARGHEZZA_CELLA = 80;
    private final int ALTEZZA_CELLA = 80;
    private final int OFFSET_X = 50;
    private final int OFFSET_Y = 100;

    public GamePanel(GameWindow window) {
        this.window = window;
        setLayout(new BorderLayout());

        // Intercettiamo il click del mouse per posizionare i difensori sulla griglia
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX() - OFFSET_X;
                int mouseY = e.getY() - OFFSET_Y;

                // Controlliamo se il click è avvenuto all'interno dei confini della griglia
                if (mouseX >= 0 && mouseX < COLONNE * LARGHEZZA_CELLA && mouseY >= 0 && mouseY < RIGHE * ALTEZZA_CELLA) {
                    int colonnaCliccata = mouseX / LARGHEZZA_CELLA;
                    int rigaCliccata = mouseY / ALTEZZA_CELLA;
                    
                    System.out.println("[GUI] Tentativo di piazzamento in Riga: " + rigaCliccata + ", Colonna: " + colonnaCliccata);
                    // TODO: Quando le classi dei difensori concreti saranno pronte, istanzieremo 
                    // la mucca corretta in base a 'difensoreSelezionato' e chiameremo GameController.getInstance().schiera(...)
                }
            }
        });

        // Loop di gioco basato su javax.swing.Timer impostato a ~30 FPS (circa 33 millisecondi per frame)
        gameTimer = new Timer(33, e -> {
            // Se la partita è finita (es. salute stalla a zero), fermiamo il gioco
            if (GameController.getInstance().isGameOver()) {
                fermaGioco();
                window.mostraPannello("GAME_OVER");
                return;
            }

            // Avanzamento logico e refresh grafico
            GameController.getInstance().tick();
            repaint();
        });
    }

    /** Avvia il loop di gioco. */
    public void avviaGioco() {
        gameTimer.start();
    }

    /** Ferma il loop di gioco. */
    public void fermaGioco() {
        gameTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Attiviamo l'antialiasing per rendere le forme geometriche più smussate e pulite
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- 1. DISEGNO HUD IN ALTO ---
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), 65);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Fieno (Balle): " + GameController.getInstance().getBalleDiFieno(), 30, 40);
        g2.drawString("Integrità Stalla: " + GameController.getInstance().getStalla().getIntegritaSistema() + "%", 260, 40);
        g2.drawString("Ondata Corrente: " + GameController.getInstance().getOndataCorrente(), 550, 40);
        
        if (GameController.getInstance().isInPausa()) {
            g2.setColor(Color.YELLOW);
            g2.drawString("[PAUSA PREPARAZIONE]", 780, 40);
        }

        // --- 2. DISEGNO GRIGLIA DI GIOCO ---
        for (int r = 0; r < RIGHE; r++) {
            for (int c = 0; c < COLONNE; c++) {
                // Alterniamo i colori del prato (chiaro/scuro) per dare l'effetto a scacchiera
                if ((r + c) % 2 == 0) {
                    g2.setColor(new Color(50, 205, 50)); // Verde lime/prato chiaro
                } else {
                    g2.setColor(new Color(34, 139, 34)); // Verde foresta/prato scuro
                }
                
                // La corsia centrale (riga index 2) la facciamo marrone come percorso preferenziale di terra
                if (r == 2) {
                    g2.setColor(new Color(139, 69, 19));
                }

                int xCella = OFFSET_X + c * LARGHEZZA_CELLA;
                int yCella = OFFSET_Y + r * ALTEZZA_CELLA;

                g2.fillRect(xCella, yCella, LARGHEZZA_CELLA, ALTEZZA_CELLA);
                g2.setColor(new Color(0, 0, 0, 40)); // Bordo leggermente scuro per staccare le celle
                g2.drawRect(xCella, yCella, LARGHEZZA_CELLA, ALTEZZA_CELLA);
            }
        }

        // --- 3. DISEGNO UNITA BOVINE (DIFENSORI) ---
        for (UnitaBovina mucca : GameController.getInstance().getUnita()) {
            int xMucca = OFFSET_X + mucca.getColonna() * LARGHEZZA_CELLA + 8;
            int yMucca = OFFSET_Y + mucca.getRiga() * ALTEZZA_CELLA + 8;
            BufferedImage sprite = Sprites.of(mucca);
            g2.drawImage(sprite, xMucca, yMucca, 64, 64, null);
        }

        // --- 4. DISEGNO INSETTI MUTANTI (NEMICI) ---
        for (InsettoMutante insetto : GameController.getInstance().getInsetti()) {
            int xInsetto = OFFSET_X + (int) (insetto.getColonna() * LARGHEZZA_CELLA) + 8;
            int yInsetto = OFFSET_Y + (insetto.getRiga() * ALTEZZA_CELLA) + 8;
            BufferedImage sprite = Sprites.of(insetto);
            g2.drawImage(sprite, xInsetto, yInsetto, 64, 64, null);
        }
    }
}
