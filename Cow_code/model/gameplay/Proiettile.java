package MuccheAllaRiscossa.model.gameplay;

/**
 * Proiettile sparato da una unita bovina lungo la sua corsia.
 *
 * Avanza verso destra ad ogni tick; quando colpisce un insetto
 * puo disattivarsi (colpo singolo) oppure continuare a viaggiare
 * se e di tipo "colpisce tutti" (es. il vitello lanciato dalla Vacca).
 */
public class Proiettile {

    private int danno;
    private double velocita;

    /** Riga (corsia) in cui viaggia il proiettile, da 0 a 4 */
    private int riga;

    /** Posizione orizzontale corrente sulla griglia */
    private double colonna;

    /** False quando il proiettile ha colpito o e uscito dalla griglia */
    private boolean attivo;

    /** Se true il proiettile attraversa tutta la corsia colpendo ogni insetto */
    private boolean colpisceTutti;

    public Proiettile(int danno, double velocita, int riga, double colonna, boolean colpisceTutti) {
        this.danno         = danno;
        this.velocita      = velocita;
        this.riga          = riga;
        this.colonna       = colonna;
        this.colpisceTutti = colpisceTutti;
        this.attivo        = true;
    }

    /**
     * Fa avanzare il proiettile di un tick.
     * Se esce dalla griglia (colonna >= 10) si disattiva.
     */
    public void muovi() {
        if (!attivo) return;
        this.colonna += velocita;
        if (colonna >= Griglia.COLONNE) {
            this.attivo = false;
        }
    }

    /**
     * Chiamato quando il proiettile colpisce un bersaglio.
     * Se non e di tipo "colpisce tutti", si disattiva dopo il primo impatto.
     */
    public void segnaColpito() {
        if (!colpisceTutti) {
            this.attivo = false;
        }
    }

    /* ---------- Getter / Setter ---------- */

    public boolean isAttivo()       { return attivo; }
    public int     getDanno()       { return danno; }
    public double  getVelocita()    { return velocita; }
    public int     getRiga()        { return riga; }
    public double  getColonna()     { return colonna; }
    public boolean isColpisceTutti() { return colpisceTutti; }

    public void setAttivo(boolean attivo) { this.attivo = attivo; }
}
