package MuccheAllaRiscossa.view;

import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * GameWindow: La finestra principale di gioco (JFrame).
 * Utilizza un CardLayout per permettere lo switch rapido e pulito
 * tra le diverse schermate (Menu, Gioco, Game Over, Vittoria).
 */
public class GameWindow extends JFrame {
    
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    public GameWindow() {
        // Impostazioni della finestra principale
        setTitle("Mucche alla Riscossa!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 600);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo
        setResizable(false);         // Dimensione fissa come da specifiche

        // Layout a schede per gestire i pannelli
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        add(mainContainer);
    }

    /**
     * Sostituisce il pannello attualmente visibile con quello richiesto.
     * @param nomePannello Il nome identificativo del pannello (es. "MENU", "GAME")
     */
    public void mostraPannello(String nomePannello) {
        cardLayout.show(mainContainer, nomePannello);
    }

    /**
     * Ritorna il contenitore principale per permettere il wiring dei pannelli nel Main.
     */
    public JPanel getMainContainer() {
        return mainContainer;
    }
}
