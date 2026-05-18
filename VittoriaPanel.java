package MuccheAllaRiscossa.view;

import javax.swing.*;
import java.awt.*;

/**
 * VittoriaPanel: Schermata celebrativa quando il giocatore vince la partita.
 */
public class VittoriaPanel extends JPanel {

    public VittoriaPanel(GameWindow window) {
        // Sfondo color oro/giallo per festeggiare
        setBackground(new Color(255, 215, 0));
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;

        // Titolo Vittoria
        JLabel lblVittoria = new JLabel("VITTORIA!");
        lblVittoria.setFont(new Font("Arial", Font.BOLD, 52));
        lblVittoria.setForeground(new Color(139, 69, 19)); // Marrone scuro per contrasto
        gbc.gridy = 0;
        add(lblVittoria, gbc);

        // Messaggio di complimenti
        JLabel lblComplimenti = new JLabel("Hai respinto tutte le ondate! Le mucche hanno trionfato sugli insetti.");
        lblComplimenti.setFont(new Font("Arial", Font.BOLD, 16));
        lblComplimenti.setForeground(Color.BLACK);
        gbc.gridy = 1;
        add(lblComplimenti, gbc);

        // Bottone per tornare al menu
        JButton btnMenu = new JButton("Torna al Menu");
        btnMenu.setFont(new Font("Arial", Font.PLAIN, 18));
        btnMenu.setPreferredSize(new Dimension(180, 45));
        btnMenu.addActionListener(e -> window.mostraPannello("MENU"));
        gbc.gridy = 2;
        add(btnMenu, gbc);
    }
}
