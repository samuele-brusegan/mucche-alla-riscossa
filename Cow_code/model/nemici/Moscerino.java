package MuccheAllaRiscossa.model.nemici;

/**
 * Moscerino: piccolo, sfuggente, schiva i colpi muovendosi a zig-zag.
 * Poca salute ma difficile da colpire.
 */
public class Moscerino extends InsettoMutante {

    /** Ampiezza dell'oscillazione laterale durante il movimento. */
    private double ampiezzaZigZag;

    public Moscerino(int riga) {
        super(25, 0.8, riga);
        this.ampiezzaZigZag = 0.4;
    }

    /** Schiva: probabilità di annullare un colpo, notifica l'evasione. */
    public void evadi() {
        System.out.println("[Moscerino#" + id + "] Zig-zag! Schivata di ampiezza " + ampiezzaZigZag);
        notifyObservers("MOSCERINO_EVADE:" + id);
    }

    public double getAmpiezzaZigZag() { return ampiezzaZigZag; }
}
