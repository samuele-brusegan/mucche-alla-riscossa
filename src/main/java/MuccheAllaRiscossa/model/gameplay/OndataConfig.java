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
    /** Numero totale di ondate previste (oltre questa il giocatore vince). */
    public static final int ONDATA_FINALE = 8;

    /** Indica se questa specifica configurazione e' una "Grande Orda" intermedia. */
    private boolean grandeOrda;
    public boolean isGrandeOrda() { return grandeOrda; }

    /**
     * Genera una Grande Orda di intensita' calibrata sull'ondata appena conclusa.
     * Tanti nemici contemporaneamente (delay ridotto) ma di tipo gia' incontrato,
     * cosi non spostano la curva di difficolta' ma fanno da test di sopravvivenza.
     */
    public static OndataConfig creaGrandeOrda(int dopoOndata) {
        List<InsettoMutante> lista = new ArrayList<>();
        // Numero di nemici cresce con il livello: 5 → 7 → 9 → 11 → 13 → 11 → 9 → 7
        int target = (dopoOndata <= 4) ? 5 + (dopoOndata - 1) * 2
                                       : 13 - (dopoOndata - 5) * 2;
        if (target < 5) target = 5;
        // Composizione: tante zanzare per gestione delle corsie, qualche moscerino,
        // mosconi solo se gia' visti, mai grantafani nelle orde (resterebbero boss-only)
        int r = 0;
        for (int n = 0; n < target; n++) {
            int riga = r % Griglia.RIGHE; r++;
            if (dopoOndata >= 6 && n % 4 == 0) lista.add(new Moscone(riga));
            else if (dopoOndata >= 3 && n % 3 == 0) lista.add(new Moscerino(riga));
            else lista.add(new Zanzara(riga));
        }
        OndataConfig conf = new OndataConfig(dopoOndata, lista, 12); // delay molto basso
        conf.grandeOrda = true;
        return conf;
    }

    /**
     * Curva di intensita a campana: si parte con un solo nemico, si cresce
     * fino al picco (orda di W5) e poi si scende verso un finale "boss-like"
     * con pochi ma fortissimi nemici (GranTafano in W7-W8).
     *
     * Il delay tra spawn e' alto nelle ondate iniziali (i nemici arrivano
     * uno alla volta) e si accorcia verso il picco per dare la sensazione
     * dell'orda; nelle ondate finali risale per dare respiro fra i boss.
     */
    public static OndataConfig creaOndata(int livello) {
        List<InsettoMutante> lista = new ArrayList<>();

        switch (livello) {

            case 1:
                // Tutorial vivente: poche zanzare, ben distanziate per imparare.
                lista.add(new Zanzara(2));
                lista.add(new Zanzara(2));
                lista.add(new Zanzara(2));
                return new OndataConfig(1, lista, 120);

            case 2:
                // Coppie alternate, tempi ancora ampi.
                lista.add(new Zanzara(1));
                lista.add(new Zanzara(3));
                lista.add(new Zanzara(0));
                lista.add(new Zanzara(4));
                lista.add(new Zanzara(2));
                return new OndataConfig(2, lista, 100);

            case 3:
                // Si comincia a fare sul serio: entrano i Moscerini, ondata piu lunga.
                lista.add(new Zanzara(0));
                lista.add(new Moscerino(2));
                lista.add(new Zanzara(4));
                lista.add(new Moscerino(1));
                lista.add(new Zanzara(3));
                lista.add(new Moscerino(0));
                lista.add(new Zanzara(2));
                return new OndataConfig(3, lista, 90);

            case 4:
                // Introduzione del Moscone corazzato.
                lista.add(new Zanzara(0));
                lista.add(new Moscerino(1));
                lista.add(new Moscone(2));
                lista.add(new Zanzara(3));
                lista.add(new Moscerino(4));
                lista.add(new Moscone(0));
                lista.add(new Zanzara(2));
                lista.add(new Moscerino(3));
                lista.add(new Zanzara(1));
                return new OndataConfig(4, lista, 80);

            case 5:
                // PICCO: tante onde di nemici, su tutte le corsie, piu intervallato di una Grande Orda.
                for (int r = 0; r < Griglia.RIGHE; r++) lista.add(new Zanzara(r));
                lista.add(new Moscerino(0));
                lista.add(new Moscerino(4));
                lista.add(new Moscerino(2));
                lista.add(new Moscone(2));
                lista.add(new Moscone(1));
                lista.add(new Moscone(3));
                for (int r = 0; r < Griglia.RIGHE; r++) lista.add(new Zanzara(r));
                return new OndataConfig(5, lista, 60);

            case 6:
                // Cala il numero ma i nemici sono piu duri.
                lista.add(new Moscone(1));
                lista.add(new Moscone(3));
                lista.add(new Moscerino(2));
                lista.add(new Zanzara(0));
                lista.add(new Zanzara(4));
                lista.add(new Moscone(0));
                lista.add(new Moscerino(4));
                lista.add(new Moscone(2));
                return new OndataConfig(6, lista, 100);

            case 7:
                // Compare il GranTafano. Pochi nemici grossi, ma con scorta.
                lista.add(new Moscone(2));
                lista.add(new GranTafano(2));
                lista.add(new Moscone(0));
                lista.add(new Moscerino(1));
                lista.add(new Moscone(4));
                lista.add(new Moscerino(3));
                return new OndataConfig(7, lista, 110);

            case 8:
                // Finale: due GranTafani con scorta di Mosconi e moscerini di disturbo.
                lista.add(new Moscerino(0));
                lista.add(new GranTafano(1));
                lista.add(new Moscone(2));
                lista.add(new Moscerino(4));
                lista.add(new GranTafano(3));
                lista.add(new Moscone(2));
                return new OndataConfig(8, lista, 120);

            default:
                // sentinella per livelli oltre l'ottavo (non usata in gioco
                // perche' la vittoria scatta a fine ondata 8)
                for (int i = 0; i < Griglia.RIGHE; i++) {
                    lista.add(new GranTafano(i));
                    lista.add(new Moscone(i));
                }
                return new OndataConfig(livello, lista, 30);
        }
    }

    /* ---------- Getter ---------- */

    public int                  getNumeroOndata()    { return numeroOndata; }
    public List<InsettoMutante> getInsetti()         { return insetti; }
    public int                  getDelayTraInsetti() { return delayTraInsetti; }
}
