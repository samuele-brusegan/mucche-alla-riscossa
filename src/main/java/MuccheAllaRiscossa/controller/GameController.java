package MuccheAllaRiscossa.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.gameplay.BoassaEsplosiva;
import MuccheAllaRiscossa.model.gameplay.OndataConfig;
import MuccheAllaRiscossa.model.gameplay.Proiettile;
import MuccheAllaRiscossa.model.nemici.InsettoMutante;
import MuccheAllaRiscossa.model.nemici.Zanzara;
import MuccheAllaRiscossa.pattern.GameEvent;
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

    private final List<Proiettile>      proiettili;
    private final List<BoassaEsplosiva> trappole;

    // Gestione sistema ondate e risorse
    private int ondataCorrente;
    private int tickContatoreOndata;
    private boolean inPausa;
    private int tickRisorse;

    /** Fieno iniziale: copre 3 Vedeo (3×50) con 25 di scorta per il primo upgrade. */
    public static final int FIENO_INIZIALE     = 175;
    /** Bonus per ogni insetto eliminato. */
    public static final int FIENO_PER_KILL     = 40;
    /** Tick fra una generazione passiva di fieno e la successiva (~4 secondi a 30fps). */
    public static final int TICK_GENERAZIONE   = 120;
    /** Quantita di fieno generata passivamente a ogni intervallo. */
    public static final int FIENO_PASSIVO      = 15;

    private final int fienoPerUccisione = FIENO_PER_KILL;

    /** Coda degli insetti dell'ondata corrente in attesa di spawn. */
    private final Deque<InsettoMutante> spawnQueue = new ArrayDeque<>();
    /** Tick rimanenti prima del prossimo spawn dalla coda. */
    private int tickProssimoSpawn;
    /** Delay (in tick) tra uno spawn e il successivo nell'ondata corrente. */
    private int delayTraSpawn;
    /** Diventa true quando il giocatore ha completato l'ultima ondata. */
    private boolean vittoria;
    /** True se la fase corrente (o quella pendente in pausa) e' una Grande Orda. */
    private boolean modalitaGrandeOrda;
    /** Numero totale di insetti previsti nella fase corrente (per la progress bar). */
    private int insettiTotaliFase;
    /** Insetti gia' rimossi (uccisi o arrivati) dalla fase corrente. */
    private int insettiCompletatiFase;

    private GameController() {
        this.balleDiFieno = FIENO_INIZIALE;
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
        // 1. Avanzamento logica nemici, con blocco fisico da parte delle mucche.
        //    Una mucca sulla cella della destinazione del nemico lo ferma e prende un morso;
        //    Zanzara (in volo) e GranTafano (sotterraneo) attraversano comunque.
        for (InsettoMutante i : new ArrayList<>(insetti)) {
            if (!i.isVivo() || i.isArrivatoAlTarget()) continue;
            UnitaBovina muro = trovaMuccaCheBlocca(i);
            if (muro != null) {
                muro.onEvent(new GameEvent.Danno(i.getDannoMorso()));
                // niente movimento questo tick: l'insetto sta mordendo.
            } else {
                i.muovi();
            }
        }

        // 2. Decremento cooldown e scan raggio d'azione delle unita bovine.
        //    Per ogni mucca cerchiamo il primo insetto vivo sulla SUA corsia entro raggioAzione
        //    (distanza orizzontale, le mucche difendono una sola riga), settiamo il bersaglio
        //    e notifichiamo InsettoInRaggio: l'unita genera proiettile/boassa o incorna.
        for (UnitaBovina u : unita) {
            u.tick();
            if (u.getCooldown() > 0) continue;
            for (InsettoMutante i : insetti) {
                if (!i.isVivo() || i.getRiga() != u.getRiga()) continue;
                // i bovini guardano e sparano verso sx: ignoriamo nemici gia' alle spalle
                if (i.getColonna() > u.getColonna() + 0.5) continue;
                double dist = u.getColonna() - i.getColonna();
                if (dist <= u.getRaggioAzione()) {
                    u.setBersaglioCorrente(i);
                    u.onEvent(new GameEvent.InsettoInRaggio(i.getRiga(), i.getColonna()));
                    break;
                }
            }
        }

        // 3. Raccolta proiettili e boasse appena generati dalle mucche
        for (UnitaBovina u : unita) {
            proiettili.addAll(u.raccogliProiettili());
            trappole.addAll(u.raccogliBoasse());
        }

        // 4. Avanzamento proiettili + collisione "a sweep" sulla stessa riga.
        //    Confrontiamo l'intervallo [oldCol, newCol] con la posizione di ogni
        //    insetto: cosi anche un proiettile veloce come quello della Vacca
        //    (che salta 15 colonne in un tick) colpisce tutti i nemici sul percorso.
        for (Iterator<Proiettile> it = proiettili.iterator(); it.hasNext(); ) {
            Proiettile p = it.next();
            double oldCol = p.getColonna();
            p.muovi();
            double newCol = p.getColonna();
            double minCol = Math.min(oldCol, newCol) - 0.5;
            double maxCol = Math.max(oldCol, newCol) + 0.5;
            for (InsettoMutante i : insetti) {
                if (!i.isVivo() || i.getRiga() != p.getRiga()) continue;
                // le Zanzare in volo sono fuori portata dei proiettili a quota terra solo
                // se vogliamo modellarlo: per ora teniamo che tutti i proiettili le prendano.
                if (i.getColonna() >= minCol && i.getColonna() <= maxCol) {
                    i.subisciDanno(p.getDanno());
                    p.segnaColpito();
                    if (!p.isAttivo()) break;
                }
            }
            if (!p.isAttivo()) it.remove();
        }

        // 5. Calpestamento boasse: insetti sulla stessa cella attivano la trappola.
        //    Le Zanzare in volo passano sopra le boasse senza farle scattare.
        for (Iterator<BoassaEsplosiva> it = trappole.iterator(); it.hasNext(); ) {
            BoassaEsplosiva b = it.next();
            if (!b.isAttiva()) { it.remove(); continue; }
            for (InsettoMutante i : insetti) {
                if (!i.isVivo() || i.getRiga() != b.getRiga()) continue;
                if (i instanceof Zanzara z && z.isInVolo()) continue;
                if (Math.abs(i.getColonna() - b.getColonna()) < 0.5) {
                    b.attiva(i);
                    break;
                }
            }
            if (!b.isAttiva()) it.remove();
        }

        // 6. Pulizia post-tick di insetti morti/arrivati e mucche sconfitte
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
                insettiCompletatiFase++;
            }
        }
        unita.removeIf(u -> !u.isViva());

        // 5. Aggiornamento dei sistemi di gioco temporizzati
        gestisciSistemaOndate();
        gestisciSpawnInsetti();
        gestisciGenerazioneRisorse();
    }

    public boolean isGameOver() { return stalla.isFatalError(); }

    public StallaTorreControllo getStalla()   { return stalla; }
    public List<UnitaBovina>    getUnita()    { return Collections.unmodifiableList(unita); }
    public List<InsettoMutante> getInsetti()  { return Collections.unmodifiableList(insetti); }
    public List<Proiettile>     getProiettili() { return Collections.unmodifiableList(proiettili); }
    public List<BoassaEsplosiva> getTrappole()  { return Collections.unmodifiableList(trappole); }
    public int getOndataCorrente() { return ondataCorrente; }
    public boolean isInPausa() { return inPausa; }
    public boolean isModalitaGrandeOrda() { return modalitaGrandeOrda; }

    /** Progresso della fase corrente in [0, 1] (insetti completati / totali). */
    public double getProgressoFase() {
        if (insettiTotaliFase <= 0) return 0.0;
        double p = (double) insettiCompletatiFase / insettiTotaliFase;
        return p < 0 ? 0 : (p > 1 ? 1 : p);
    }

    /**
     * Riporta il singleton allo stato di inizio partita (fieno, salute, liste,
     * ondate, coda di spawn) e ripristina anche la stalla. Da usare quando il
     * giocatore preme "Riprova" o "Gioca" dal menu, per evitare di restare
     * bloccati nello stato di game over precedente.
     */
    public void nuovaPartita() {
        resetForTesting();
        stalla.reset();
    }

    /** Reset utile per test/demo: ricrea lo stato iniziale. NON usare in produzione. */
    public void resetForTesting() {
        this.balleDiFieno = FIENO_INIZIALE;
        this.saluteStalla = 100;
        this.unita.clear();
        this.insetti.clear();
        this.proiettili.clear();
        this.trappole.clear();
        this.ondataCorrente = 0;
        this.tickContatoreOndata = 0;
        this.inPausa = true;
        this.tickRisorse = 0;
        this.spawnQueue.clear();
        this.tickProssimoSpawn = 0;
        this.delayTraSpawn = 0;
        this.vittoria = false;
        this.modalitaGrandeOrda = false;
        this.insettiTotaliFase = 0;
        this.insettiCompletatiFase = 0;
        // resetta lo stato della stalla (integrità, fatalError) ricostruendone i campi via reflection-free:
        // più semplice: reimposta i callback a null e applica un evento "fittizio" non basta;
        // accettiamo che la stalla mantenga lo stato perché il singleton la possiede final.
        // Per i test che hanno bisogno di una stalla "fresca", istanziarla a parte.
    }
/**
     * Calcola la distanza euclidea tra un'unità bovina e un insetto mutante.
     */
    /**
     * Cerca una mucca viva sulla cella in cui l'insetto sta per entrare
     * (stessa riga, colonna intera prossima). Restituisce null se la cella
     * e' libera o se l'insetto la attraversa (volo/scavo).
     */
    private UnitaBovina trovaMuccaCheBlocca(InsettoMutante i) {
        if (i.attraversaMucche()) return null;
        // colonna in cui finirebbe il nemico al prossimo passo
        int cellaProssima = (int) Math.floor(i.getColonna() + i.getVelocita());
        for (UnitaBovina u : unita) {
            if (!u.isViva()) continue;
            if (u.getRiga() == i.getRiga() && u.getColonna() == cellaProssima) return u;
        }
        return null;
    }

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
        // Pausa standard tra ondate regolari (~5s) e pausa piu corta prima della Grande Orda (~3s)
        final int PAUSA_REGOLARE = 150;
        final int PAUSA_ORDA     = 90;
        if (vittoria) return; // partita gia conclusa
        if (inPausa) {
            tickContatoreOndata++;
            int soglia = modalitaGrandeOrda ? PAUSA_ORDA : PAUSA_REGOLARE;
            if (tickContatoreOndata >= soglia) {
                // Vittoria solo dopo che l'ultima ondata regolare e' stata respinta
                // (la W8 non e' seguita da una Grande Orda).
                if (!modalitaGrandeOrda && ondataCorrente >= OndataConfig.ONDATA_FINALE) {
                    vittoria = true;
                    System.out.println("[VITTORIA] Tutte le " + OndataConfig.ONDATA_FINALE
                            + " ondate respinte. Le mucche hanno trionfato!");
                    return;
                }
                inPausa = false;
                tickContatoreOndata = 0;
                if (modalitaGrandeOrda) {
                    avviaGrandeOrda();
                } else {
                    avviaOndata(ondataCorrente + 1);
                }
            }
        } else {
            if (isOndataCompletata()) {
                inPausa = true;
                tickContatoreOndata = 0;
                // Dopo un'ondata regolare 1..7 segue la Grande Orda; dopo l'orda
                // si torna alla regolare successiva. La W8 chiude la partita.
                if (modalitaGrandeOrda) {
                    modalitaGrandeOrda = false;
                } else if (ondataCorrente < OndataConfig.ONDATA_FINALE) {
                    modalitaGrandeOrda = true;
                }
            }
        }
    }

    /** Avvia la Grande Orda calibrata sull'ondata regolare appena conclusa. */
    private void avviaGrandeOrda() {
        OndataConfig orda = OndataConfig.creaGrandeOrda(ondataCorrente);
        spawnQueue.clear();
        spawnQueue.addAll(orda.getInsetti());
        delayTraSpawn = orda.getDelayTraInsetti();
        tickProssimoSpawn = 0;
        insettiTotaliFase = orda.getInsetti().size();
        insettiCompletatiFase = 0;
        System.out.println("[ORDA] Grande Orda dopo W" + ondataCorrente + ": "
                + insettiTotaliFase + " nemici!");
    }

    /** True quando il giocatore ha respinto tutte le ondate previste. */
    public boolean isVittoria() { return vittoria; }

    /**
     * Avvia l'ondata iniettando i nemici nel tabellone.
     */
    public void avviaOndata(int numero) {
        this.ondataCorrente = numero;
        OndataConfig config = OndataConfig.creaOndata(numero);
        spawnQueue.clear();
        spawnQueue.addAll(config.getInsetti());
        delayTraSpawn = config.getDelayTraInsetti();
        tickProssimoSpawn = 0; // primo insetto al prossimo tick
        insettiTotaliFase = config.getInsetti().size();
        insettiCompletatiFase = 0;
        System.out.println("Avvio Ondata Numero: " + ondataCorrente
                + " (" + spawnQueue.size() + " insetti, delay " + delayTraSpawn + ")");
    }

    /**
     * Spawna un insetto dalla coda quando il countdown lo consente.
     * Va invocato a ogni tick durante l'ondata attiva.
     */
    private void gestisciSpawnInsetti() {
        if (spawnQueue.isEmpty()) return;
        if (tickProssimoSpawn > 0) {
            tickProssimoSpawn--;
            return;
        }
        InsettoMutante prossimo = spawnQueue.pollFirst();
        if (prossimo != null) aggiungiInsetto(prossimo);
        tickProssimoSpawn = delayTraSpawn;
    }

    /**
     * Controlla se tutti gli insetti dell'ondata attuale sono stati eliminati,
     * sia in coda di spawn che già in campo.
     */
    public boolean isOndataCompletata() {
        return insetti.isEmpty() && spawnQueue.isEmpty();
    }

    /**
     * Rilascia fieno passivo al giocatore ogni {@link #TICK_GENERAZIONE} tick.
     */
    private void gestisciGenerazioneRisorse() {
        tickRisorse++;
        if (tickRisorse >= TICK_GENERAZIONE) {
            aggiungiFieno(FIENO_PASSIVO);
            tickRisorse = 0;
        }
    }
}
