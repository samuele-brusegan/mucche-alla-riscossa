package MuccheAllaRiscossa.view;

import javax.swing.*;
import java.awt.*;

/**
 * MenuPanel: Schermata iniziale del gioco con sfondo verde prato.
 * Presenta il titolo, la lore e i pulsanti principali di interazione.
 */
public class MenuPanel extends JPanel {

    public MenuPanel(GameWindow window) {
        // Impostiamo uno sfondo verde prato
        setBackground(new Color(34, 139, 34));
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Margini tra gli elementi
        gbc.gridx = 0;

        // Titolo grande del gioco
        JLabel lblTitolo = new JLabel("MUCCHE ALLA RISCOSSA");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 46));
        lblTitolo.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(lblTitolo, gbc);

        // Sottotitolo con la lore / storia del gioco
        JLabel lblLore = new JLabel("Gli insetti mutanti vogliono invadere la stalla. Schiera le tue bovine e difendi il fieno!");
        lblLore.setFont(new Font("Arial", Font.ITALIC, 16));
        lblLore.setForeground(new Color(220, 220, 220));
        gbc.gridy = 1;
        add(lblLore, gbc);

        // Bottone "GIOCA"
        JButton btnGioca = new JButton("GIOCA");
        btnGioca.setFont(new Font("Arial", Font.BOLD, 22));
        btnGioca.setPreferredSize(new Dimension(180, 50));
        btnGioca.addActionListener(e -> {
            // Cambia pannello mostrando quello di gioco reale
            window.mostraPannello("GAME");
        });
        gbc.gridy = 2;
        add(btnGioca, gbc);

        // Bottone "ESCI"
        JButton btnEsci = new JButton("ESCI");
        btnEsci.setFont(new Font("Arial", Font.PLAIN, 18));
        btnEsci.setPreferredSize(new Dimension(120, 40));
        btnEsci.addActionListener(e -> System.exit(0));
        gbc.gridy = 3;
        add(btnEsci, gbc);
    }
}
