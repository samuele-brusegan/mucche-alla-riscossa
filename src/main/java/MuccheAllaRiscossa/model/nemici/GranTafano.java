package MuccheAllaRiscossa.model.nemici;

import MuccheAllaRiscossa.pattern.GameEvent;

/**
 * GranTafano: il boss. Quando entra in difficoltà si sotterra e
 * riemerge più avanti, scavalcando una porzione della corsia.
 */
public class GranTafano extends InsettoMutante {

    /** True mentre l'insetto è in fase di scavo (invulnerabile, invisibile). */
    private boolean isSotterraneo;

    /** Salute iniziale, serve per capire quando scendere sotto il 50% */
    private int saluteIniziale;

    /** Contatore dei tick passati sotto terra */
    private int tickSotterraneo;

    public GranTafano(int riga) {
        super(400, 0.3, riga);
        this.isSotterraneo   = false;
        this.saluteIniziale  = salute;
        this.tickSotterraneo = 0;
    }

    /**
     * Override di avanza(): se la salute scende sotto il 50% e non e
     * gia sotterraneo, si sotterra. Dopo 3 tick sottoterra riemerge
     * avanzando di 2 colonne extra.
     */
    @Override
    protected void avanza() {
        if (isSotterraneo) {
            tickSotterraneo++;
            if (tickSotterraneo >= 3) {
                // riemerge avanzando di 2 colonne extra
                scava();
                this.colonna += 2.0;
                System.out.println("[GranTafano#" + id + "] Riemerso 2 colonne piu avanti!");
            }
            // mentre e sotto terra non si muove normalmente
            return;
        }

        // se scende sotto meta vita e non si e ancora sotterrato, si sotterra
        if (salute < saluteIniziale / 2 && salute > 0) {
            scava();
            return;
        }

        // movimento normale
        super.avanza();
    }

    /** Inizia/termina la fase di scavo, notifica il cambio di stato. */
    public void scava() {
        this.isSotterraneo = !isSotterraneo;
        if (isSotterraneo) {
            this.tickSotterraneo = 0;
        }
        System.out.println("[GranTafano#" + id + "] " + (isSotterraneo ? "Si sotterra!" : "Riemerge!"));
        notifyObservers(new GameEvent.TafanoScava(id, isSotterraneo));
    }

    /** Mentre è sotterraneo non subisce danno. */
    @Override
    public void subisciDanno(int danno) {
        if (isSotterraneo) {
            System.out.println("[GranTafano#" + id + "] Sotterraneo, il colpo manca!");
            return;
        }
        super.subisciDanno(danno);
    }

    public boolean isSotterraneo()   { return isSotterraneo; }
    public int     getSaluteIniziale() { return saluteIniziale; }
}
