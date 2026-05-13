package MuccheAllaRiscossa.model.nemici;

/**
 * Zanzara: insetto leggero e veloce che può volare oltre le balle di fieno.
 * Salute bassa ma difficile da fermare con difese a terra.
 */
public class Zanzara extends InsettoMutante {

    /** Altezza di volo (0 = terra, >0 = sorvola le difese). */
    private double quotaVolo;

    public Zanzara(int riga) {
        super(40, 0.6, riga);
        this.quotaVolo = 1.5;
    }

    /** Sale di quota per superare le balle: lo notifichiamo agli osservatori. */
    public void volaOltreBalle() {
        System.out.println("[Zanzara#" + id + "] Vola oltre le balle a quota " + quotaVolo);
        notifyObservers("ZANZARA_IN_VOLO:" + id + ":riga:" + riga);
    }

    public double getQuotaVolo() { return quotaVolo; }
}
