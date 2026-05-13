package MuccheAllaRiscossa.model.difensori;

/**
 * Mucca:
 * rilascia "Boasse" sul terreno davanti a sé. 
 * Ogni insetto che ci calpesta subisce danno.
 */

public class Mucca extends UnitaBovina {

    private double rallentamentoBoassa; /**rallentamento applicato all'insetto che la calpesta(0.0 fermo; 1.0 normale velocità) */

    private int dannoTrappola;

    public Mucca() {
        super("Mucca Beatrice", 75, 1.0, 150);
        this.rallentamentoBoassa = 0.3; // l'insetto rallenta al 30% della sua velocità
        this.dannoTrappola = 50;
    }

    /**attacca() per Beatrice significa: piazzare una boassa nella cella davanti. Si attiva periodicamente(ogni X secondi).*/
    @Override
    public void attacca() {
        piazzaTrappola();
    }

    //La boassa rimane sul campo finché un insetto non ci cammina sopra.
    public void piazzaTrappola() {
        System.out.println("[" + nome + "] Boassa piazzata in colonna " + (colonna + 1) + "! Odore garanzia.");
        // Todo: creare oggetto BoassaEsplosiva e aggiungerlo alla griglia tramite GameController
    }

    public double getRallentamentoBoassa() { return rallentamentoBoassa; }
    public int    getDannoTrappola()       { return dannoTrappola; }
}
