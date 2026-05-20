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

    /** True dopo che il Tafano ha gia' scavato una volta: lo scavo e' un'abilita' "one-shot". */
    private boolean haGiaScavato;

    public GranTafano(int riga) {
        // boss: tanta vita ma andatura lenta, cosi il giocatore ha tempo di reagire.
        super(400, 0.025, riga);
        this.isSotterraneo   = false;
        this.saluteIniziale  = salute;
        this.tickSotterraneo = 0;
        this.dannoMorso      = 25; // boss: morso devastante, mangia un Vedeo (100hp) in 4 tick
    }

    /** Mentre e sotterraneo passa attraverso qualsiasi mucca: e' invulnerabile e invisibile. */
    @Override
    public boolean attraversaMucche() { return isSotterraneo; }

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
                // riemerge avanzando di 1 colonna extra (lo scavo non e piu un teleport devastante)
                scava();
                this.colonna += 1.0;
                System.out.println("[GranTafano#" + id + "] Riemerso 1 colonna piu avanti!");
            }
            // mentre e sotto terra non si muove normalmente
            return;
        }

        // Sotto al 50% scava una sola volta in tutta la vita: dopo l'emersione
        // resta in superficie fino alla morte (niente loop di immersioni continue).
        if (!haGiaScavato && salute < saluteIniziale / 2 && salute > 0) {
            haGiaScavato = true;
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
