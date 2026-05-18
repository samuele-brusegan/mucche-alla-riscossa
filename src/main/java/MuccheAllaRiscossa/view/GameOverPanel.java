package MuccheAllaRiscossa.view;

import javax.swing.*;
import java.awt.*;

/**
 * GameOverPanel: Schermata che appare quando la stalla viene distrutta.
 * Mostra il messaggio di sconfitta e permette di tornare al menu principale.
 */
public class GameOverPanel extends JPanel {

    public GameOverPanel(GameWindow window) {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;

        // Scritta Game Over d'impatto
        JLabel lblGameOver = new JLabel("GAME OVER");
        lblGameOver.setFont(new Font("Arial", Font.BOLD, 52));
        lblGameOver.setForeground(Color.RED);
        gbc.gridy = 0;
        add(lblGameOver, gbc);

        // Messaggio di sventura
        JLabel lblDettaglio = new JLabel("Gli insetti mutanti hanno devastato la stalla. Il fieno è andato perduto!");
        lblDettaglio.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDettaglio.setForeground(Color.WHITE);
        gbc.gridy = 1;
        add(lblDettaglio, gbc);

        // Bottone per riprovare
        JButton btnRiprova = new JButton("Riprova");
        btnRiprova.setFont(new Font("Arial", Font.BOLD, 20));
        btnRiprova.setPreferredSize(new Dimension(160, 45));
        btnRiprova.addActionListener(e -> {
            // TODO: In futuro qui si potrebbe resettare lo stato del GameController 
            // prima di tornare al menu principale
            window.mostraPannello("MENU");
        });
        gbc.gridy = 2;
        add(btnRiprova, gbc);
    }
}
