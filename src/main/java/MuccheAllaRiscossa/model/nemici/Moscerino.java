package MuccheAllaRiscossa.model.nemici;

import MuccheAllaRiscossa.model.RandomSource;
import MuccheAllaRiscossa.pattern.GameEvent;

/**
 * Moscerino: piccolo, sfuggente, schiva i colpi muovendosi a zig-zag.
 * Poca salute ma difficile da colpire.
 *
 * L'esito dell'evasione dipende da una {@link RandomSource} iniettabile,
 * così i test possono forzare schivata/non-schivata in modo deterministico.
 */
public class Moscerino extends InsettoMutante {

    /** Ampiezza dell'oscillazione laterale durante il movimento. */
    private double ampiezzaZigZag;

    /** Probabilita di schivare un colpo (0.0 - 1.0) */
    private static final double PROB_EVASIONE = 0.30;

    /** Sorgente di numeri casuali per la decisione di evasione. */
    private final RandomSource random;

    public Moscerino(int riga) {
        this(riga, RandomSource.DEFAULT);
    }

    /** Costruttore di test: permette di iniettare una sorgente deterministica. */
    public Moscerino(int riga, RandomSource random) {
        super(25, 0.8, riga);
        this.ampiezzaZigZag = 0.4;
        this.random = random;
    }

    /**
     * Override di subisciDanno: prima di applicare il danno,
     * il moscerino ha una probabilita del 30% di schivare.
     */
    @Override
    public void subisciDanno(int danno) {
        if (random.nextDouble() < PROB_EVASIONE) {
            evadi();
            return; // danno annullato
        }
        super.subisciDanno(danno);
    }

    /** Schiva: probabilità di annullare un colpo, notifica l'evasione. */
    public void evadi() {
        System.out.println("[Moscerino#" + id + "] Zig-zag! Schivata di ampiezza " + ampiezzaZigZag);
        notifyObservers(new GameEvent.MoscerinoEvade(id));
    }

    public double getAmpiezzaZigZag() { return ampiezzaZigZag; }
}
