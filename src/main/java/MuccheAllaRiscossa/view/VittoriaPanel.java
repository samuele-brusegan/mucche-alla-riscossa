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
 * VittoriaPanel: schermata celebrativa quando il giocatore vince la partita.
 */
public class VittoriaPanel extends VBox {

    public VittoriaPanel(GameWindow window) {
        setAlignment(Pos.CENTER);
        setSpacing(25);
        setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 215, 0), CornerRadii.EMPTY, null)));

        // Titolo Vittoria
        Label lblVittoria = new Label("VITTORIA!");
        lblVittoria.setFont(Font.font("Arial", FontWeight.BOLD, 52));
        lblVittoria.setTextFill(Color.rgb(139, 69, 19)); // Marrone scuro per contrasto

        // Messaggio di complimenti
        Label lblComplimenti = new Label(
                "Hai respinto tutte le ondate! Le mucche hanno trionfato sugli insetti.");
        lblComplimenti.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblComplimenti.setTextFill(Color.BLACK);

        // Bottone per tornare al menu (resetta lo stato per evitare di ripartire vincenti)
        Button btnMenu = new Button("Torna al Menu");
        btnMenu.setFont(Font.font("Arial", 18));
        btnMenu.setPrefSize(180, 45);
        btnMenu.setOnAction(e -> {
            MuccheAllaRiscossa.controller.GameController.getInstance().nuovaPartita();
            window.mostraPannello("MENU");
        });

        getChildren().addAll(lblVittoria, lblComplimenti, btnMenu);
    }
}
