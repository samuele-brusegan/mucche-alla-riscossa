package MuccheAllaRiscossa.model.difensori;

import MuccheAllaRiscossa.model.gameplay.Proiettile;

/** VitellinoVedeo:
 * Spara "Zampe di Balsa" agli insetti nella sua corsia.
 * È la mucca più economica e facile da usare(diciamo livello base).
 */

public class VitellinoVedeo extends UnitaBovina {

    /** uso final per dire che non cambia mai) */
    private final String arma = "Zampa di Balsa";

    private int dannoPerColpo;

    /** Ultimo proiettile sparato (il controller lo raccoglie) */
    private Proiettile ultimoProiettile;

    public VitellinoVedeo() {
        super(
            "Vedeo",  // nome
            50,  // costoFieno (abbastanza abbordabile)
            5.0,  // raggioAzione, prende 5 celle
            100  // vita
        );
        this.dannoPerColpo = 20;
    }

    /** Vitellino spara una zampa di balsa verso destra nella sua corsia.*/
    @Override
    public void attacca() {
        System.out.println("[" + nome + "] Sparo una " + arma + "! Danno: " + dannoPerColpo);

        // creo il proiettile una cella avanti rispetto alla mia posizione
        this.ultimoProiettile = new Proiettile(
                dannoPerColpo,
                1.0,           // velocita standard
                riga,
                colonna + 1,
                false          // colpisce solo il primo insetto
        );
    }

    /** Restituisce l'ultimo proiettile creato (puo essere null se non ha ancora sparato) */
    public Proiettile getUltimoProiettile() { return ultimoProiettile; }

    public String getArma()        { return arma; }
    public int    getDannoPerColpo() { return dannoPerColpo; }
}
