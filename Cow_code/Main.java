package MuccheAllaRiscossa;

import MuccheAllaRiscossa.controller.GameController;
import MuccheAllaRiscossa.model.difensori.Mucca;
import MuccheAllaRiscossa.model.difensori.MuccaCornuta;
import MuccheAllaRiscossa.model.difensori.Vacca;
import MuccheAllaRiscossa.model.difensori.VitellinoVedeo;
import MuccheAllaRiscossa.model.nemici.GranTafano;
import MuccheAllaRiscossa.model.nemici.Moscerino;
import MuccheAllaRiscossa.model.nemici.Moscone;
import MuccheAllaRiscossa.model.nemici.Zanzara;

/**
 * Entry point del gioco. Per ora esegue una demo testuale che mostra
 * il pattern Observer in funzione: insetti che avanzano e notificano
 * la stalla quando la raggiungono.
 */
public final class Main {

    public static void main(String[] args) {
        GameController gc = GameController.getInstance();
        gc.aggiungiFieno(500);

        // schiera qualche difensore
        gc.schiera(new VitellinoVedeo(), 0, 1);
        gc.schiera(new Mucca(),          1, 2);
        gc.schiera(new MuccaCornuta(),   2, 3);
        gc.schiera(new Vacca(),          3, 4);

        // ondata di nemici
        gc.aggiungiInsetto(new Zanzara(0));
        gc.aggiungiInsetto(new Moscone(1));
        gc.aggiungiInsetto(new Moscerino(2));
        gc.aggiungiInsetto(new GranTafano(3));

        System.out.println("=== Inizio partita ===");
        int tick = 0;
        while (!gc.isGameOver() && tick < 100) {
            tick++;
            System.out.println("--- Tick " + tick + " ---");
            gc.tick();
        }
        System.out.println("=== Fine partita al tick " + tick + " ===");
        System.out.println("Stalla integrità: " + gc.getStalla().getIntegritaSistema());
        System.out.println("Fieno residuo:    " + gc.getBalleDiFieno());
    }
}
