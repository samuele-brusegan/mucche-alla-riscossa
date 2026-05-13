package MuccheAllaRiscossa.model.difensori;

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
        incorna();
    }

    public void incorna() {
        System.out.println("[" + nome + "] CORNATA! Danno corpo a corpo: " + dannoCarica);
        // Todo: trovare InsettoMutante nella stessa cella e applicare dannoCarica
    }

    public int getDannoCarica() { return dannoCarica; }
}
