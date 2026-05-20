package MuccheAllaRiscossa.view.assets;

import MuccheAllaRiscossa.model.difensori.Mucca;
import MuccheAllaRiscossa.model.difensori.MuccaCornuta;
import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.difensori.Vacca;
import MuccheAllaRiscossa.model.difensori.VitellinoVedeo;
import MuccheAllaRiscossa.model.nemici.GranTafano;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.model.nemici.Moscerino;
import MuccheAllaRiscossa.model.nemici.Moscone;
import MuccheAllaRiscossa.model.nemici.Zanzara;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Mappatura semplice da classi del model a sprite JavaFX.
 *
 * Il rendering ({@code GamePanel}) chiama {@link #of(UnitaBovina)} o
 * {@link #of(InsettoMutante)} e ottiene un'immagine, sia che il PNG
 * reale sia presente sia che venga generato un placeholder colorato.
 */
public final class Sprites {

    private Sprites() {}

    public static Image of(UnitaBovina u) {
        return switch (u) {
            case VitellinoVedeo v -> ResourceLoader.sprite("vitellino", Color.rgb(135, 206, 235), 'V');
            case Mucca m          -> ResourceLoader.sprite("mucca",     Color.rgb(176, 130, 90),  'M');
            case MuccaCornuta mc  -> ResourceLoader.sprite("cornuta",   Color.rgb(112, 66, 20),   'C');
            case Vacca v          -> ResourceLoader.sprite("vacca",     Color.rgb(220, 220, 220), 'A');
            default               -> ResourceLoader.sprite("difensore", Color.rgb(65, 105, 225),  '?');
        };
    }

    public static Image of(InsettoMutante i) {
        return switch (i) {
            case Zanzara z    -> ResourceLoader.sprite("zanzara",   Color.rgb(120, 0, 120),   'Z');
            case Moscerino m  -> ResourceLoader.sprite("moscerino", Color.rgb(60, 60, 60),    'M');
            case Moscone mo   -> ResourceLoader.sprite("moscone",   Color.rgb(40, 40, 100),   'O');
            case GranTafano g -> ResourceLoader.sprite("grantafano", Color.rgb(160, 30, 30),  'T');
            default           -> ResourceLoader.sprite("nemico",    Color.rgb(220, 20, 60),   '?');
        };
    }

    /** Sprite del proiettile (zampa di balsa / vitello lanciato). */
    public static Image proiettile() {
        return ResourceLoader.sprite("proiettile", Color.YELLOW, '*');
    }

    /** Sprite della boassa esplosiva (trappola di Beatrice). */
    public static Image boassa() {
        return ResourceLoader.sprite("boassa", Color.rgb(80, 50, 20), 'B');
    }

    /** Tile d'erba per il fondo della corsia. Dimensione richiesta es. 80x80. */
    public static Image erba(int w, int h) {
        return ResourceLoader.svgScalato("erba", w, h);
    }

    /** Sprite della stalla da disegnare a fine corsia. */
    public static Image stalla(int w, int h) {
        return ResourceLoader.svgScalato("stalla", w, h);
    }

    /** Icona "balla di fieno" per l'HUD. */
    public static Image ballaFieno(int w, int h) {
        return ResourceLoader.svgScalato("balla_fieno", w, h);
    }
}
