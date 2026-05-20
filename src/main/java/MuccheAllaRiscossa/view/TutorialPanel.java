package MuccheAllaRiscossa.view;

import MuccheAllaRiscossa.controller.GameController;

import javafx.geometry.Insets;
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
import javafx.scene.text.TextAlignment;

/**
 * TutorialPanel: schermata mostrata prima della prima partita.
 * Spiega in poche righe regole, mucche e nemici e da' il via al gioco.
 *
 * Si entra qui dal MenuPanel cliccando "GIOCA"; il bottone "INIZIA" resetta
 * lo stato del singleton e passa alla schermata di partita.
 */
public class TutorialPanel extends VBox {

    public TutorialPanel(GameWindow window) {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(14);
        setPadding(new Insets(40, 60, 40, 60));
        setBackground(new Background(new BackgroundFill(
                Color.rgb(20, 60, 20), CornerRadii.EMPTY, null)));

        Label titolo = new Label("COME SI GIOCA");
        titolo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titolo.setTextFill(Color.WHITE);

        Label intro = etichetta(
                "Gli insetti mutanti avanzano da destra verso la tua stalla. "
              + "Schiera le mucche per fermarli prima che la raggiungano.",
                Color.rgb(230, 230, 230), 16, false);

        Label comandi = etichetta(
                "Comandi:\n"
              + "  • In alto, scegli la mucca dai 4 bottoni.\n"
              + "  • Clicca su una cella verde per piazzarla (costa fieno).\n"
              + "  • +" + GameController.FIENO_PER_KILL + " fieno per ogni insetto eliminato, +"
              + GameController.FIENO_PASSIVO + " ogni ~4 secondi.",
                Color.WHITE, 15, false);

        Label mucche = etichetta(
                "Le tue mucche (le mucche bloccano fisicamente i nemici!):\n"
              + "  • Vedeo (50)  - spara a media distanza, vita 100.\n"
              + "  • Beatrice (75) - piazza boasse che rallentano e feriscono.\n"
              + "  • Cornuta (125) - tappo corpo a corpo, vita 300: il muro.\n"
              + "  • Vacca (200)  - lancia un vitello che attraversa tutta la corsia.",
                Color.rgb(255, 230, 150), 14, true);

        Label nemici = etichetta(
                "I nemici (mordono le mucche che li bloccano):\n"
              + "  • Zanzara - vola: passa sopra le mucche e le boasse.\n"
              + "  • Moscerino - puo' schivare i colpi (30%).\n"
              + "  • Moscone - corazzato, assorbe parte del danno e morde forte.\n"
              + "  • GranTafano - boss: sotto meta' vita scava e passa sotto le mucche.",
                Color.rgb(255, 180, 180), 14, true);

        Label ondate = etichetta(
                "Le ondate sono lunghe e cresconlo per intensita'. Tra una W e l'altra "
              + "arriva una GRANDE ORDA (rosso nella barra) di nemici contemporanei. "
              + "Resisti fino alla fine dell'ondata 8 per vincere.",
                Color.rgb(220, 255, 220), 15, false);

        Button btnInizia = new Button("INIZIA");
        btnInizia.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        btnInizia.setPrefSize(200, 55);
        btnInizia.setOnAction(e -> {
            GameController.getInstance().nuovaPartita();
            window.mostraPannello("GAME");
        });

        Button btnIndietro = new Button("Indietro");
        btnIndietro.setFont(Font.font("Arial", 14));
        btnIndietro.setOnAction(e -> window.mostraPannello("MENU"));

        getChildren().addAll(titolo, intro, comandi, mucche, nemici, ondate, btnInizia, btnIndietro);
    }

    /** Crea una label con stile uniforme. */
    private Label etichetta(String testo, Color colore, double size, boolean bold) {
        Label l = new Label(testo);
        l.setFont(bold ? Font.font("Arial", FontWeight.BOLD, size) : Font.font("Arial", size));
        l.setTextFill(colore);
        l.setTextAlignment(TextAlignment.LEFT);
        l.setWrapText(true);
        return l;
    }
}
