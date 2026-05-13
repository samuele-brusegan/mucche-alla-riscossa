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

    private GameController() {
        this.balleDiFieno = 100;
        this.saluteStalla = 100;
        this.stalla       = new StallaTorreControllo();
        this.unita        = new ArrayList<>();
        this.insetti      = new ArrayList<>();
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
    public void tick() {
        for (InsettoMutante i : new ArrayList<>(insetti)) {
            i.muovi();
        }
        // pulizia post-tick
        for (Iterator<InsettoMutante> it = insetti.iterator(); it.hasNext(); ) {
            InsettoMutante i = it.next();
            if (!i.isVivo() || i.isArrivatoAlTarget()) {
                // smaglia gli observer per evitare leak
                for (UnitaBovina u : unita) i.detach(u);
                i.detach(stalla);
                it.remove();
            }
        }
        unita.removeIf(u -> !u.isViva());
    }

    public boolean isGameOver() { return stalla.isFatalError(); }

    public StallaTorreControllo getStalla()   { return stalla; }
    public List<UnitaBovina>    getUnita()    { return Collections.unmodifiableList(unita); }
    public List<InsettoMutante> getInsetti()  { return Collections.unmodifiableList(insetti); }

    /** Reset utile per test/demo: ricrea lo stato iniziale. NON usare in produzione. */
    void resetForTesting() {
        this.balleDiFieno = 100;
        this.saluteStalla = 100;
        this.unita.clear();
        this.insetti.clear();
    }
}
