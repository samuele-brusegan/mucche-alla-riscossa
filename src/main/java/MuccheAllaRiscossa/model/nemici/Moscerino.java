package MuccheAllaRiscossa.model.nemici;

/**
 * Moscerino: piccolo, sfuggente, schiva i colpi muovendosi a zig-zag.
 * Poca salute ma difficile da colpire.
 */
public class Moscerino extends InsettoMutante {

    /** Ampiezza dell'oscillazione laterale durante il movimento. */
    private double ampiezzaZigZag;

    /** Probabilita di schivare un colpo (0.0 - 1.0) */
    private static final double PROB_EVASIONE = 0.30;

    public Moscerino(int riga) {
        super(25, 0.8, riga);
        this.ampiezzaZigZag = 0.4;
    }

    /**
     * Override di subisciDanno: prima di applicare il danno,
     * il moscerino ha una probabilita del 30% di schivare.
     */
    @Override
    public void subisciDanno(int danno) {
        if (Math.random() < PROB_EVASIONE) {
            evadi();
            return; // danno annullato
        }
        super.subisciDanno(danno);
    }

    /** Schiva: probabilità di annullare un colpo, notifica l'evasione. */
    public void evadi() {
        System.out.println("[Moscerino#" + id + "] Zig-zag! Schivata di ampiezza " + ampiezzaZigZag);
        notifyObservers("MOSCERINO_EVADE:" + id);
    }

    public double getAmpiezzaZigZag() { return ampiezzaZigZag; }
}
