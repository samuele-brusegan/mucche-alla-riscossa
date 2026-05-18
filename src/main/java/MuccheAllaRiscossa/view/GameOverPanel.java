package MuccheAllaRiscossa.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * GameOverPanel: schermata che appare quando la stalla viene distrutta.
 * Mostra il messaggio di sconfitta e permette di tornare al menu principale.
 */
public class GameOverPanel extends VBox {

    public GameOverPanel(GameWindow window) {
        setAlignment(Pos.CENTER);
        setSpacing(25);
        setBackground(new Background(new BackgroundFill(
                Color.BLACK, CornerRadii.EMPTY, null)));

        // Scritta Game Over d'impatto
        Label lblGameOver = new Label("GAME OVER");
        lblGameOver.setFont(Font.font("Arial", FontWeight.BOLD, 52));
        lblGameOver.setTextFill(Color.RED);

        // Messaggio di sventura
        Label lblDettaglio = new Label(
                "Gli insetti mutanti hanno devastato la stalla. Il fieno è andato perduto!");
        lblDettaglio.setFont(Font.font("Arial", 16));
        lblDettaglio.setTextFill(Color.WHITE);

        // Bottone per riprovare
        Button btnRiprova = new Button("Riprova");
        btnRiprova.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        btnRiprova.setPrefSize(160, 45);
        btnRiprova.setOnAction(e -> {
            // TODO: In futuro qui si potrebbe resettare lo stato del GameController
            // prima di tornare al menu principale
            window.mostraPannello("MENU");
        });

        getChildren().addAll(lblGameOver, lblDettaglio, btnRiprova);
    }
}
