package MuccheAllaRiscossa.model.nemici;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import MuccheAllaRiscossa.pattern.GameEvent;
import MuccheAllaRiscossa.pattern.Observer;
import MuccheAllaRiscossa.pattern.Subject;

/**
 * Classe ASTRATTA base per tutti gli insetti mutanti nemici.
 *
 * Implementa {@link Subject}: ogni insetto mantiene la propria lista di
 * osservatori (tipicamente la StallaTorreControllo e le UnitaBovina nelle
 * vicinanze) e li notifica quando avanza, viene danneggiato o arriva alla
 * stalla.
 */
public abstract class InsettoMutante implements Subject {

    /** Generatore thread-safe di identificativi univoci. */
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    /** Identificativo univoco dell'insetto. */
    protected final int id;

    /** Punti vita correnti. */
    protected int salute;

    /** Velocità di avanzamento in colonne/tick. */
    protected double velocita;

    /** True se ha raggiunto la stalla (game over per il giocatore). */
    protected boolean arrivatoAlTarget;

    /** Riga (corsia) su cui si muove, 0-4. */
    protected int riga;

    /** Colonna corrente sulla griglia. */
    protected double colonna;

    /** Velocita originale, prima di qualsiasi rallentamento */
    protected double velocitaBase;

    /** Tick rimanenti di rallentamento (0 = nessun rallentamento attivo) */
    protected int tickRallentamento;

    /**
     * Danno inflitto a una mucca per ogni tick di "morso" (quando si trova sulla
     * stessa cella di una mucca che lo blocca). Le sottoclassi possono alzarlo.
     */
    protected int dannoMorso = 5;

    /** Lista degli osservatori registrati. */
    private final List<Observer> osservatori = new ArrayList<>();

    protected InsettoMutante(int salute, double velocita, int riga) {
        this.id               = NEXT_ID.getAndIncrement();
        this.salute           = salute;
        this.velocita         = velocita;
        this.velocitaBase     = velocita;
        this.riga             = riga;
        this.colonna          = 0.0;
        this.arrivatoAlTarget = false;
        this.tickRallentamento = 0;
    }

    /* ---------- API Subject ---------- */

    @Override
    public void attach(Observer obs) {
        if (obs != null && !osservatori.contains(obs)) {
            osservatori.add(obs);
        }
    }

    @Override
    public void detach(Observer obs) {
        osservatori.remove(obs);
    }

    @Override
    public void notifyObservers(GameEvent evento) {
        // Iteriamo su una copia per consentire detach() durante l'update
        for (Observer o : new ArrayList<>(osservatori)) {
            o.onEvent(evento);
        }
    }

    /* ---------- Comportamento ---------- */

    /**
     * Avanza l'insetto di un tick. Il movimento concreto (volo, scavo,
     * zig-zag, ...) viene delegato alle sottoclassi tramite avanza().
     * Quando colonna raggiunge la stalla, viene notificato l'evento.
     */
    public void muovi() {
        if (arrivatoAlTarget || salute <= 0) return;
        avanza();
        if (colonna >= COLONNA_STALLA) {
            setArrivato();
        }
    }

    /** Colonna oltre la quale si considera raggiunta la stalla. */
    public static final int COLONNA_STALLA = 9;

    /**
     * Comportamento di avanzamento specifico della sottoclasse.
     * Per default incrementa colonna in base alla velocità.
     * Se il rallentamento e attivo, usa la velocita ridotta.
     */
    protected void avanza() {
        if (tickRallentamento > 0) {
            this.colonna += velocita; // velocita gia ridotta dal metodo rallenta()
            tickRallentamento--;
            if (tickRallentamento == 0) {
                // il rallentamento e finito, ripristino la velocita originale
                this.velocita = velocitaBase;
            }
        } else {
            this.colonna += velocita;
        }
    }

    /** Segna l'insetto come arrivato alla stalla e notifica l'invasione. */
    public void setArrivato() {
        this.arrivatoAlTarget = true;
        notifyObservers(new GameEvent.InvasioneStalla(getClass().getSimpleName(), id));
    }

    /** Applica danno all'insetto e notifica eventuale morte. */
    public void subisciDanno(int danno) {
        this.salute -= danno;
        if (this.salute <= 0) {
            notifyObservers(new GameEvent.InsettoMorto(getClass().getSimpleName(), id));
        }
    }

    /**
     * Rallenta l'insetto per un certo numero di tick.
     * Il fattore indica la percentuale di velocita mantenuta (es. 0.3 = 30%).
     */
    public void rallenta(double fattore) {
        this.velocita = velocitaBase * fattore;
        this.tickRallentamento = 5; // dura 5 tick
        System.out.println("[" + getClass().getSimpleName() + "#" + id
                + "] Rallentato! Velocita: " + velocita + " per " + tickRallentamento + " tick");
    }

    public boolean isVivo()             { return salute > 0; }
    public int     getId()              { return id; }
    public int     getSalute()          { return salute; }
    public double  getVelocita()        { return velocita; }
    public double  getVelocitaBase()    { return velocitaBase; }
    public int     getRiga()            { return riga; }
    public double  getColonna()         { return colonna; }
    public boolean isArrivatoAlTarget() { return arrivatoAlTarget; }
    public int     getDannoMorso()      { return dannoMorso; }

    /**
     * True se l'insetto in questo momento puo' attraversare le mucche senza
     * essere bloccato (volo, scavo). Le sottoclassi sovrascrivono per i casi
     * particolari; il default si limita al fatto di essere vivo.
     */
    public boolean attraversaMucche() { return false; }
}
