package MuccheAllaRiscossa.model.gameplay;

import java.util.ArrayList;
import java.util.List;

import MuccheAllaRiscossa.model.nemici.GranTafano;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.model.nemici.Moscerino;
import MuccheAllaRiscossa.model.nemici.Moscone;
import MuccheAllaRiscossa.model.nemici.Zanzara;

/**
 * Configurazione di una singola ondata di insetti.
 *
 * Contiene la lista degli insetti da spawnare e il delay (in tick)
 * tra uno spawn e l'altro. Il metodo factory {@link #creaOndata(int)}
 * genera ondate predefinite con difficolta crescente.
 */
public class OndataConfig {

    private int numeroOndata;
    private List<InsettoMutante> insetti;

    /** Tick di attesa tra lo spawn di un insetto e il successivo */
    private int delayTraInsetti;

    public OndataConfig(int numeroOndata, List<InsettoMutante> insetti, int delayTraInsetti) {
        this.numeroOndata   = numeroOndata;
        this.insetti        = insetti;
        this.delayTraInsetti = delayTraInsetti;
    }

    /**
     * Factory method che genera un'ondata predefinita in base al livello.
     * La difficolta cresce con piu insetti, tipi piu resistenti e delay ridotti.
     *
     * @param livello numero dell'ondata (da 1 in su)
     * @return configurazione dell'ondata corrispondente
     */
    public static OndataConfig creaOndata(int livello) {
        List<InsettoMutante> lista = new ArrayList<>();

        switch (livello) {

            case 1:
                // ondata facile: solo zanzare sparse sulle corsie
                lista.add(new Zanzara(0));
                lista.add(new Zanzara(1));
                lista.add(new Zanzara(2));
                return new OndataConfig(1, lista, 8);

            case 2:
                // si aggiungono i moscerini, un po' piu fastidiosi
                lista.add(new Zanzara(0));
                lista.add(new Moscerino(1));
                lista.add(new Zanzara(2));
                lista.add(new Moscerino(3));
                lista.add(new Zanzara(4));
                return new OndataConfig(2, lista, 6);

            case 3:
                // arriva il moscone corazzato insieme ai piccoli
                lista.add(new Moscerino(0));
                lista.add(new Moscone(1));
                lista.add(new Zanzara(2));
                lista.add(new Moscerino(3));
                lista.add(new Moscone(4));
                lista.add(new Zanzara(0));
                return new OndataConfig(3, lista, 5);

            case 4:
                // ondata mista, roba seria: tanti insetti e meno tempo
                lista.add(new Moscone(0));
                lista.add(new Moscone(1));
                lista.add(new Moscerino(2));
                lista.add(new Zanzara(3));
                lista.add(new Moscone(4));
                lista.add(new Moscerino(0));
                lista.add(new Zanzara(1));
                lista.add(new Moscone(2));
                return new OndataConfig(4, lista, 4);

            case 5:
                // boss finale: il GranTafano con scorta di mosconi e moscerini
                lista.add(new Moscone(0));
                lista.add(new Moscerino(1));
                lista.add(new GranTafano(2));
                lista.add(new Moscone(3));
                lista.add(new Moscerino(4));
                lista.add(new Zanzara(0));
                lista.add(new GranTafano(1));
                lista.add(new Moscone(2));
                lista.add(new Moscerino(3));
                lista.add(new Zanzara(4));
                return new OndataConfig(5, lista, 3);

            default:
                // per livelli oltre il 5 generiamo un'ondata brutale
                for (int i = 0; i < Griglia.RIGHE; i++) {
                    lista.add(new GranTafano(i));
                    lista.add(new Moscone(i));
                }
                return new OndataConfig(livello, lista, 2);
        }
    }

    /* ---------- Getter ---------- */

    public int                  getNumeroOndata()    { return numeroOndata; }
    public List<InsettoMutante> getInsetti()         { return insetti; }
    public int                  getDelayTraInsetti() { return delayTraInsetti; }
}
