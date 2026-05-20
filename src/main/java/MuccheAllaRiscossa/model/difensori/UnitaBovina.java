package MuccheAllaRiscossa.model.difensori;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import MuccheAllaRiscossa.model.gameplay.BoassaEsplosiva;
import MuccheAllaRiscossa.model.gameplay.Proiettile;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.pattern.GameEvent;
import MuccheAllaRiscossa.pattern.Observer;

/**
 * UnitaBovina, ovvero la Classe ASTRATTA base per tutte le mucche.
 *
 * Implementa l'interfaccia {@link Observer} del pattern Observer:
 * ogni unità bovina è osservatore degli eventi pubblicati dai Subject
 * del gioco (tipicamente gli InsettoMutante) e reagisce di conseguenza
 * (attaccando, subendo danno, ecc.).
 *
 * Eventi gestiti da onEvent():
 *   - GameEvent.InsettoInRaggio -> l'unità entra in IN_ATTACCO e chiama attacca()
 *   - GameEvent.Danno           -> l'unità subisce danno
 *   - GameEvent.FinePartita     -> l'unità smette di reagire
 *   - altri eventi              -> ignorati (estendibile dal team)
 */
public abstract class UnitaBovina implements Observer {

    /** Generatore thread-safe di identificativi univoci per le unità. */
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    /** Identificativo univoco della singola unità (immutabile). */
    protected final int id;

    /** Nome della pargola guerriera(vedeo, vacca, mucca...) */
    protected String nome;

    /** Quanto costa piazzarla in "Balle di Fieno" */
    protected int costoFieno;

    /** Raggio d'azione in cui il suo colpo ha effetto */
    protected double raggioAzione;

    /** Riga della griglia in cui è piazzata, sono 5 corsie (quindi da 0-4)*/
    protected int riga;

    /** Colonna della griglia in cui l'abbiamo posizionata */
    protected int colonna;

    /** Punti vita della mucca */
    protected int vita;

    /** Stato corrente dell'unità (vedi {@link StatoUnita}). */
    protected StatoUnita stato;

    /** Tick rimanenti prima di poter attaccare di nuovo */
    protected int cooldown;

    /** Valore massimo del cooldown (si resetta a questo dopo ogni attacco) */
    protected int cooldownMax;

    /** Proiettili pronti da raccogliere (il controller li prende e svuota la lista) */
    protected List<Proiettile> proiettiliPronti;

    /** Boasse pronte da raccogliere (usata solo dalle Mucche Beatrice) */
    protected List<BoassaEsplosiva> boassePronte;

    /**
     * Bersaglio corrente designato dal controller prima di triggerare l'attacco.
     * Necessario per attacchi corpo a corpo (es. MuccaCornuta) che devono sapere
     * quale insetto colpire; le mucche a distanza possono ignorarlo.
     */
    protected InsettoMutante bersaglioCorrente;

    /** nelle altre classi richiamo il costruttore tramite super(...)*/
    public UnitaBovina(String nome, int costoFieno, double raggioAzione, int vita) {
        this.id                = NEXT_ID.getAndIncrement();
        this.nome              = nome;
        this.costoFieno        = costoFieno;
        this.raggioAzione      = raggioAzione;
        this.vita              = vita;
        this.stato             = StatoUnita.ATTIVA;
        this.cooldown          = 0;
        this.cooldownMax       = 3; // di default attacca ogni 3 tick
        this.proiettiliPronti  = new ArrayList<>();
        this.boassePronte      = new ArrayList<>();
    }

    /** Imposta l'insetto bersaglio prima del prossimo attacco (chiamato dal controller). */
    public void setBersaglioCorrente(InsettoMutante b) { this.bersaglioCorrente = b; }

    /** Metodo astratto che ogni mucca avrà differente*/
    public abstract void attacca();


    /** Metodi che hanno tutte le mucche*/
    public void riceviDanno(int danno) {
        this.vita -= danno;
        if (this.vita <= 0) {
            this.stato = StatoUnita.MORTA;
            System.out.println("[MUCCA] " + nome + " ci ha lasciato, muuuu!");
        } else {
            this.stato = StatoUnita.FERITA;
        }
    }

    /** Controlla se la mucca è ancora viva */
    public boolean isViva() {
        return this.vita > 0;
    }

    /**
     * Decrementa il cooldown di 1. Va chiamato dal controller ad ogni tick
     * per gestire la frequenza di attacco delle unita.
     */
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    /**
     * Reagisce a un evento del Subject osservato.
     *
     * Le unità morte ignorano qualsiasi evento. Gli altri vengono dispatchati
     * via pattern matching; eventi non gestiti sono ignorati di proposito,
     * così l'enum si può estendere senza rompere i client esistenti.
     */
    @Override
    public void onEvent(GameEvent evento) {
        if (evento == null || stato == StatoUnita.MORTA) {
            return;
        }
        switch (evento) {
            case GameEvent.InsettoInRaggio ignored -> {
                if (cooldown == 0) {
                    this.stato = StatoUnita.IN_ATTACCO;
                    attacca();
                    this.cooldown = cooldownMax;
                }
            }
            case GameEvent.Danno d   -> riceviDanno(d.quantita());
            case GameEvent.FinePartita ignored -> this.stato = StatoUnita.MORTA;
            default -> { /* eventi non rilevanti per le unità: ignorati */ }
        }
    }


    public int          getId()           { return id; }
    public String       getNome()         { return nome; }
    public int          getCostoFieno()   { return costoFieno; }
    public double       getRaggioAzione() { return raggioAzione; }
    public int          getRiga()         { return riga; }
    public int          getColonna()      { return colonna; }
    public int          getVita()         { return vita; }
    public StatoUnita   getStato()        { return stato; }
    public int          getCooldown()     { return cooldown; }

    public void setRiga(int riga)         { this.riga = riga; }
    public void setColonna(int colonna)   { this.colonna = colonna; }
    public void setCooldownMax(int max)   { this.cooldownMax = max; }

    /** Restituisce i proiettili pronti e svuota la lista */
    public List<Proiettile> raccogliProiettili() {
        List<Proiettile> copia = new ArrayList<>(proiettiliPronti);
        proiettiliPronti.clear();
        return copia;
    }

    /** Restituisce le boasse pronte e svuota la lista */
    public List<BoassaEsplosiva> raccogliBoasse() {
        List<BoassaEsplosiva> copia = new ArrayList<>(boassePronte);
        boassePronte.clear();
        return copia;
    }
}
