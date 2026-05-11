/** VitellinoVedeo:
 * Spara "Zampe di Balsa" agli insetti nella sua corsia.
 * È la mucca più economica e facile da usare(diciamo livello base).
 */

public class VitellinoVedeo extends UnitaBovina {

    /** uso final per dire che non cambia mai) */
    private final String arma = "Zampa di Balsa";

    private int dannoPerColpo;

 
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

        // Todo: creare "Proiettile" e aggiungerlo alla lista proiettili del GameController
    }


    public String getArma()        { return arma; }
    public int    getDannoPerColpo() { return dannoPerColpo; }
