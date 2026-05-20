package MuccheAllaRiscossa.view;

import javafx.application.Platform;
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
 * MenuPanel: schermata iniziale con sfondo verde prato, titolo, lore e i
 * due pulsanti principali (GIOCA / ESCI).
 */
public class MenuPanel extends VBox {

    public MenuPanel(GameWindow window) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setBackground(new Background(new BackgroundFill(
                Color.rgb(34, 139, 34), CornerRadii.EMPTY, null)));

        // Titolo grande del gioco
        Label lblTitolo = new Label("MUCCHE ALLA RISCOSSA");
        lblTitolo.setFont(Font.font("Arial", FontWeight.BOLD, 46));
        lblTitolo.setTextFill(Color.WHITE);

        // Sottotitolo con la lore / storia del gioco
        Label lblLore = new Label(
                "Gli insetti mutanti vogliono invadere la stalla. Schiera le tue bovine e difendi il fieno!");
        lblLore.setFont(Font.font("Arial", javafx.scene.text.FontPosture.ITALIC, 16));
        lblLore.setTextFill(Color.rgb(220, 220, 220));

        // Bottone "GIOCA"
        Button btnGioca = new Button("GIOCA");
        btnGioca.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        btnGioca.setPrefSize(180, 50);
        // GIOCA porta al tutorial; lo stato viene resettato quando si preme INIZIA
        // (o direttamente dal TutorialPanel).
        btnGioca.setOnAction(e -> window.mostraPannello("TUTORIAL"));

        // Bottone "ESCI"
        Button btnEsci = new Button("ESCI");
        btnEsci.setFont(Font.font("Arial", 18));
        btnEsci.setPrefSize(120, 40);
        btnEsci.setOnAction(e -> Platform.exit());

        getChildren().addAll(lblTitolo, lblLore, btnGioca, btnEsci);
    }
}
