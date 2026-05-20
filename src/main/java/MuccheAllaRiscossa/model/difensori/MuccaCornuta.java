package MuccheAllaRiscossa.model.difensori;

import MuccheAllaRiscossa.model.nemici.InsettoMutante;

/** MuccaCornuta
 * Non attacca a distanza: aspetta che un insetto entri nella sua cella
 * e poi lo incorna con danni devastanti. Ha molta vita per fare da muro.
 */

public class MuccaCornuta extends UnitaBovina {

    private int dannoCarica;

    public MuccaCornuta() {
        super("Mucca Cornuta Assunta", 125, 0.5, 300);
        this.dannoCarica = 80;
    }

    //incorna il primo insetto che si trova nella sua stessa cella o in quella adiacente.

    @Override
    public void attacca() {
        // Se il controller ha designato un bersaglio applichiamo davvero il danno corpo a corpo;
        // altrimenti facciamo il colpo "a vuoto" per retrocompatibilita con i test.
        if (bersaglioCorrente != null) incorna(bersaglioCorrente);
        else                            incorna();
    }

    /** Versione senza parametri: stampa l'attacco (retrocompatibilita) */
    public void incorna() {
        System.out.println("[" + nome + "] CORNATA! Danno corpo a corpo: " + dannoCarica);
    }

    /** Versione con bersaglio: infligge danno diretto all'insetto */
    public void incorna(InsettoMutante insetto) {
        System.out.println("[" + nome + "] CORNATA! Danno corpo a corpo: " + dannoCarica);
        if (insetto != null) {
            insetto.subisciDanno(dannoCarica);
        }
    }

    public int getDannoCarica() { return dannoCarica; }
}
