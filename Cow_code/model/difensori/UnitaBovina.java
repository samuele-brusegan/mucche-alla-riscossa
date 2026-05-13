package MuccheAllaRiscossa.model.difensori;

import java.util.concurrent.atomic.AtomicInteger;

import MuccheAllaRiscossa.pattern.Observer;

/**
 * UnitaBovina, ovvero la Classe ASTRATTA base per tutte le mucche.
 *
 * Implementa l'interfaccia {@link Observer} del pattern Observer:
 * ogni unità bovina è osservatore degli eventi pubblicati dai Subject
 * del gioco (tipicamente gli InsettoMutante) e reagisce di conseguenza
 * (attaccando, subendo danno, ecc.).
 *
 * Protocollo dei messaggi attesi su update():
 *   - "INSETTO_IN_RAGGIO[:info]"  -> l'unità entra in stato IN_ATTACCO e chiama attacca()
 *   - "DANNO:<n>"                 -> l'unità subisce <n> punti di danno
 *   - "FINE_PARTITA"              -> l'unità smette di reagire
 *   - altri messaggi              -> ignorati (estendibile dal team)
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

    /** nelle altre classi richiamo il costruttore tramite super(...)*/
    public UnitaBovina(String nome, int costoFieno, double raggioAzione, int vita) {
        this.id           = NEXT_ID.getAndIncrement();
        this.nome         = nome;
        this.costoFieno   = costoFieno;
        this.raggioAzione = raggioAzione;
        this.vita         = vita;
        this.stato        = StatoUnita.ATTIVA;
    }

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
     * Reagisce a una notifica del Subject osservato.
     *
     * Le unità morte ignorano qualsiasi messaggio. Per gli altri messaggi,
     * il dispatching avviene confrontando il prefisso del messaggio col
     * protocollo concordato. Nuovi eventi possono essere aggiunti senza
     * rompere i client esistenti grazie al ramo di default che ignora.
     *
     * @param messaggio messaggio inviato dal Subject (non null).
     */
    @Override
    public void update(String messaggio) {
        if (messaggio == null || stato == StatoUnita.MORTA) {
            return;
        }

        if (messaggio.startsWith("INSETTO_IN_RAGGIO")) {
            this.stato = StatoUnita.IN_ATTACCO;
            attacca();
            return;
        }

        if (messaggio.startsWith("DANNO:")) {
            try {
                int danno = Integer.parseInt(messaggio.substring("DANNO:".length()).trim());
                riceviDanno(danno);
            } catch (NumberFormatException e) {
                System.err.println("[" + nome + "] messaggio DANNO malformato: " + messaggio);
            }
            return;
        }

        if (messaggio.equals("FINE_PARTITA")) {
            this.stato = StatoUnita.MORTA; // smette di reagire alle notifiche successive
            return;
        }
        // messaggi sconosciuti: ignorati di proposito (estendibile dal team)
    }


    public int          getId()           { return id; }
    public String       getNome()         { return nome; }
    public int          getCostoFieno()   { return costoFieno; }
    public double       getRaggioAzione() { return raggioAzione; }
    public int          getRiga()         { return riga; }
    public int          getColonna()      { return colonna; }
    public int          getVita()         { return vita; }
    public StatoUnita   getStato()        { return stato; }

    public void setRiga(int riga)         { this.riga = riga; }
    public void setColonna(int colonna)   { this.colonna = colonna; }
}
