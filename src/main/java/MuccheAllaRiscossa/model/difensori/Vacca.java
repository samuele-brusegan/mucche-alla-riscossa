package MuccheAllaRiscossa.model.difensori;

import MuccheAllaRiscossa.model.gameplay.Proiettile;

/** Vacca
 * Colpisce tutti i nemici nella sua corsia con danno elevato.
 * È la più potente (e più cara)
 */
public class Vacca extends UnitaBovina {

    private int velocitaLancio;

    private int dannoVitello;

    /** Ultimo proiettile lanciato (il controller lo raccoglie) */
    private Proiettile ultimoProiettile;

    public Vacca() {
        super("Vacca", 200, 8.0, 200);
        this.velocitaLancio = 15; // Molto veloce
        this.dannoVitello   = 40;
    }

    // Il vitello lanciato attraversa tutta la corsia colpendo ogni insetto sul percorso.
    
    @Override
    public void attacca() {
        lancioDelVitello();
    }

    /** Spara un vitello verso destra nella corsia */
    public void lancioDelVitello() {
        System.out.println("[" + nome + "] LANCIO VITELLO! Velocità: " + velocitaLancio + " | Danno: " + dannoVitello);

        // il vitello attraversa tutta la corsia e colpisce tutti gli insetti che incontra
        this.ultimoProiettile = new Proiettile(
                dannoVitello,
                velocitaLancio,
                riga,
                colonna + 1,
                true           // colpisce tutti i nemici in linea
        );
    }

    /** Restituisce l'ultimo proiettile creato (puo essere null) */
    public Proiettile getUltimoProiettile() { return ultimoProiettile; }

    public int getVelocitaLancio() { return velocitaLancio; }
    public int getDannoVitello()   { return dannoVitello; }
}
