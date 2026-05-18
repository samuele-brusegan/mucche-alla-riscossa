package MuccheAllaRiscossa.model.gameplay;

import MuccheAllaRiscossa.model.nemici.InsettoMutante;

/**
 * BoassaEsplosiva: trappola piazzata dalla Mucca sul terreno.
 *
 * Resta sulla cella finche un insetto non ci cammina sopra.
 * Quando viene calpestata infligge danno e applica un rallentamento
 * al malcapitato, poi si disattiva.
 */
public class BoassaEsplosiva {

    private int danno;

    /** Fattore di rallentamento: 0.0 = fermo, 1.0 = velocita invariata */
    private double rallentamento;

    private int riga;
    private int colonna;

    /** True finche la trappola non e stata calpestata */
    private boolean attiva;

    public BoassaEsplosiva(int danno, double rallentamento, int riga, int colonna) {
        this.danno          = danno;
        this.rallentamento  = rallentamento;
        this.riga           = riga;
        this.colonna        = colonna;
        this.attiva         = true;
    }

    /**
     * Attiva la trappola su un insetto che ci e finito sopra.
     * Infligge danno e rallenta l'insetto, poi la boassa si consuma.
     */
    public void attiva(InsettoMutante insetto) {
        if (!attiva || insetto == null) return;

        System.out.println("[BOASSA] Insetto #" + insetto.getId()
                + " ha calpestato la boassa in (" + riga + "," + colonna + ")! Puzza tremenda!");

        insetto.subisciDanno(danno);
        insetto.rallenta(rallentamento);

        this.attiva = false;
    }

    /* ---------- Getter / Setter ---------- */

    public boolean isAttiva()        { return attiva; }
    public int     getDanno()        { return danno; }
    public double  getRallentamento() { return rallentamento; }
    public int     getRiga()         { return riga; }
    public int     getColonna()      { return colonna; }

    public void setAttiva(boolean attiva) { this.attiva = attiva; }
}
