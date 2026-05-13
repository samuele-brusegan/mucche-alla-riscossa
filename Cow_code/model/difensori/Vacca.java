package MuccheAllaRiscossa.model.difensori;

/** Vacca
 * Colpisce tutti i nemici nella sua corsia con danno elevato.
 * È la più potente (e più cara)
 */
public class Vacca extends UnitaBovina {

    private int velocitaLancio;

    private int dannoVitello;

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
        // Todo: creare Proiettile(colpisce tutti i nemici in linea)
    }

    public int getVelocitaLancio() { return velocitaLancio; }
    public int getDannoVitello()   { return dannoVitello; }
}
