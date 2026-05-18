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

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Mappatura semplice da classi del model a sprite.
 *
 * Il rendering ({@code GamePanel}) chiama {@link #of(UnitaBovina)} o
 * {@link #of(InsettoMutante)} e ottiene un'immagine, sia che il PNG
 * reale sia presente sia che venga generato un placeholder colorato.
 */
public final class Sprites {

    private Sprites() {}

    public static BufferedImage of(UnitaBovina u) {
        return switch (u) {
            case VitellinoVedeo v -> ResourceLoader.sprite("vitellino", new Color(135, 206, 235), 'V');
            case Mucca m          -> ResourceLoader.sprite("mucca",     new Color(176, 130, 90),  'M');
            case MuccaCornuta mc  -> ResourceLoader.sprite("cornuta",   new Color(112, 66, 20),   'C');
            case Vacca v          -> ResourceLoader.sprite("vacca",     new Color(220, 220, 220), 'A');
            default               -> ResourceLoader.sprite("difensore", new Color(65, 105, 225),  '?');
        };
    }

    public static BufferedImage of(InsettoMutante i) {
        return switch (i) {
            case Zanzara z    -> ResourceLoader.sprite("zanzara",   new Color(180, 60, 60),   'Z');
            case Moscerino m  -> ResourceLoader.sprite("moscerino", new Color(120, 90, 50),   'm');
            case Moscone mo   -> ResourceLoader.sprite("moscone",   new Color(60, 60, 60),    'O');
            case GranTafano t -> ResourceLoader.sprite("tafano",    new Color(160, 30, 30),   'T');
            default           -> ResourceLoader.sprite("nemico",    new Color(220, 20, 60),   '?');
        };
    }
}
