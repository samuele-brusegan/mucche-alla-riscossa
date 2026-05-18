package MuccheAllaRiscossa.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.view.StallaTorreControllo;

/**
 * GameController: Singleton, unico punto di controllo per le risorse
 * (Balle di Fieno), per la schiera di unità bovine e per l'ondata di
 * insetti mutanti.
 *
 * Si occupa anche del wiring del pattern Observer: ogni nuovo insetto
 * viene "attached" alla stalla e alle unità bovine, così le notifiche
 * (movimento, invasione, danno) raggiungono i destinatari giusti.
 */
public final class GameController {

    /** Holder per inizializzazione lazy thread-safe del Singleton. */
    private static final class Holder {
        private static final GameController INSTANCE = new GameController();
    }

    private int balleDiFieno;
    private int saluteStalla;

    private final StallaTorreControllo stalla;
    private final List<UnitaBovina>    unita;
    private final List<InsettoMutante> insetti;

    // TODO: importare dopo il merge del Flusso 1
    // private final List<Proiettile> proiettili;
    // private final List<BoassaEsplosiva> trappole;
    private final List<Object> proiettili; // Placeholder temporaneo
    private final List<Object> trappole;    // Placeholder temporaneo

    // Gestione sistema ondate e risorse
    private int ondataCorrente;
    private int tickContatoreOndata;
    private boolean inPausa;
    private int tickRisorse;
    private final int fienoPerUccisione = 25;

    private GameController() {
        this.balleDiFieno = 100;
        this.saluteStalla = 100;
        this.stalla       = new StallaTorreControllo();
        this.unita        = new ArrayList<>();
        this.insetti      = new ArrayList<>();
        this.proiettili = new ArrayList<>();
        this.trappole   = new ArrayList<>();
        this.ondataCorrente = 0;
        this.tickContatoreOndata = 0;
        this.inPausa = true;
        this.tickRisorse = 0;
    }

    public static GameController getInstance() {
        return Holder.INSTANCE;
    }

    /* ---------- Gestione fieno ---------- */

    public void aggiungiFieno(int qta) {
        this.balleDiFieno += qta;
    }

    /**
     * Tenta di spendere {@code qta} balle di fieno.
     * @return true se l'operazione è andata a buon fine, false se fondi insufficienti.
     */
    public boolean sottraiFieno(int qta) {
        if (qta > balleDiFieno) return false;
        this.balleDiFieno -= qta;
        return true;
    }

    public int getBalleDiFieno() { return balleDiFieno; }
    public int getSaluteStalla() { return saluteStalla; }

    /* ---------- Schieramento difensori ---------- */

    /**
     * Schiera una nuova unità bovina sulla griglia, scalando il costo dal fieno.
     * Registra l'unità come observer di tutti gli insetti già in campo nella
     * stessa corsia, e viceversa.
     *
     * @return true se piazzamento riuscito, false se fieno insufficiente o cella occupata.
     */
    public boolean schiera(UnitaBovina u, int riga, int colonna) {
        if (!sottraiFieno(u.getCostoFieno())) return false;
        u.setRiga(riga);
        u.setColonna(colonna);
        unita.add(u);
        // collega l'unità agli insetti già esistenti sulla sua corsia
        for (InsettoMutante i : insetti) {
            if (i.getRiga() == riga) i.attach(u);
        }
        return true;
    }

    /* ---------- Gestione ondata ---------- */

    /**
     * Aggiunge un insetto all'ondata corrente e lo collega come Subject
     * alla stalla e a tutte le unità bovine già schierate nella sua corsia.
     */
    public void aggiungiInsetto(InsettoMutante i) {
        insetti.add(i);
        i.attach(stalla);
        for (UnitaBovina u : unita) {
            if (u.getRiga() == i.getRiga()) i.attach(u);
        }
    }

    /**
     * Esegue un tick di gioco: muove tutti gli insetti vivi.
     * Rimuove dal campo unità morte e insetti morti/arrivati al target.
     */
    /**
     * Esegue un tick di gioco: muove tutti gli insetti vivi, gestisce
     * attacchi, trappole, risorse e l'avanzamento delle ondate.
     */
    public void tick() {
        // 1. Avanzamento logica nemici
        for (InsettoMutante i : new ArrayList<>(insetti)) {
            i.muovi();
        }

        // 2. Controllo raggio d'azione delle Unità Bovine (Attacco)
        for (UnitaBovina u : unita) {
            for (InsettoMutante i : insetti) {
                if (i.isVivo() && calcolaDistanza(u, i) <= u.getRaggioAzione()) {
                    // Notifica logica "INSETTO_IN_RAGGIO"
                    // TODO: mucca genera proiettile o attacca dopo merge Flusso 1
                    break; // Si focalizza sul primo insetto in raggio
                }
            }
        }

        // 3. Controllo se un insetto calpesta una trappola (stessa riga e colonna)
        // TODO: Integrare logica reale dopo merge Flusso 1 con BoassaEsplosiva
        /*
        for (Object trappola : new ArrayList<>(trappole)) {
            for (InsettoMutante i : new ArrayList<>(insetti)) {
                // if (trappola.getRiga() == i.getRiga() && trappola.getColonna() == i.getColonna()) { ... }
            }
        }
        */

        // 4. Pulizia post-tick di insetti morti/arrivati e mucche sconfitte
        for (Iterator<InsettoMutante> it = insetti.iterator(); it.hasNext(); ) {
            InsettoMutante i = it.next();
            if (!i.isVivo() || i.isArrivatoAlTarget()) {
                for (UnitaBovina u : unita) i.detach(u);
                i.detach(stalla);
                
                // Se l'ascoltatore rileva l'evento "INSETTO_MORTO" (non arrivato alla stalla)
                if (!i.isVivo()) {
                    aggiungiFieno(fienoPerUccisione);
                    System.out.println("INSETTO_MORTO: Guadagnate " + fienoPerUccisione + " balle di fieno!");
                }
                
                it.remove();
            }
        }
        unita.removeIf(u -> !u.isViva());

        // 5. Aggiornamento dei sistemi di gioco temporizzati
        gestisciSistemaOndate();
        gestisciGenerazioneRisorse();
    }
        unita.removeIf(u -> !u.isViva());
    }

    public boolean isGameOver() { return stalla.isFatalError(); }

    public StallaTorreControllo getStalla()   { return stalla; }
    public List<UnitaBovina>    getUnita()    { return Collections.unmodifiableList(unita); }
    public List<InsettoMutante> getInsetti()  { return Collections.unmodifiableList(insetti); }
    public int getOndataCorrente() { return ondataCorrente; }
    public boolean isInPausa() { return inPausa; }

    /** Reset utile per test/demo: ricrea lo stato iniziale. NON usare in produzione. */
    void resetForTesting() {
        this.balleDiFieno = 100;
        this.saluteStalla = 100;
        this.unita.clear();
        this.insetti.clear();
    }
/**
     * Calcola la distanza euclidea tra un'unità bovina e un insetto mutante.
     */
    public double calcolaDistanza(UnitaBovina u, InsettoMutante i) {
        // Usiamo provvisoriamente righe e colonne come coordinate cartesiane discrete
        double dx = u.getColonna() - i.getColonna();
        double dy = u.getRiga() - i.getRiga();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gestisce la pausa temporizzata tra un'ondata e l'altra (150 tick = ~5 secondi).
     */
    private void gestisciSistemaOndate() {
        final int PAUSA_TICK_MAX = 150;
        if (inPausa) {
            tickContatoreOndata++;
            if (tickContatoreOndata >= PAUSA_TICK_MAX) {
                inPausa = false;
                tickContatoreOndata = 0;
                avviaOndata(ondataCorrente + 1);
            }
        } else {
            if (isOndataCompletata()) {
                inPausa = true;
                tickContatoreOndata = 0;
            }
        }
    }

    /**
     * Avvia l'ondata iniettando i nemici nel tabellone.
     */
    public void avviaOndata(int numero) {
        this.ondataCorrente = numero;
        System.out.println("Avvio Ondata Numero: " + ondataCorrente);
        // TODO: Generare e inserire le istanze degli insetti mutanti tramite aggiungiInsetto() dopo merge Flusso 1
    }

    /**
     * Controlla se tutti gli insetti dell'ondata attuale sono stati eliminati.
     */
    public boolean isOndataCompletata() {
        return insetti.isEmpty();
    }

    /**
     * Rilascia fieno passivo al giocatore ogni 150 tick di gioco.
     */
    private void gestisciGenerazioneRisorse() {
        tickRisorse++;
        if (tickRisorse >= 150) {
            aggiungiFieno(10); // +10 fieno ogni 5 secondi
            tickRisorse = 0;
        }
    }
}
