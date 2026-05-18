package MuccheAllaRiscossa.view;

import MuccheAllaRiscossa.pattern.GameEvent;
import MuccheAllaRiscossa.pattern.Observer;

/**
 * StallaTorreControllo: Observer che rappresenta la stalla del giocatore.
 *
 * Riceve gli eventi pubblicati dagli {@code InsettoMutante} (Subject) e
 * gestisce la propria "integrità di sistema". Un'invasione manda l'integrità
 * a zero (Fatal Error → game over), un {@link GameEvent.DannoStalla} la
 * riduce gradualmente.
 */
public class StallaTorreControllo implements Observer {

    /** Salute della stalla, 0..100. */
    private int integritaSistema;

    /** True quando la stalla è stata invasa (game over). */
    private boolean fatalError;

    /** Callback invocato quando scatta il fatal error (game over). */
    private Runnable onFatalError;

    /** Callback invocato ogni volta che la stalla subisce un danno (per aggiornare la GUI). */
    private Runnable onDannoSubito;

    public StallaTorreControllo() {
        this.integritaSistema = 100;
        this.fatalError = false;
    }

    @Override
    public void onEvent(GameEvent evento) {
        if (evento == null) return;
        switch (evento) {
            case GameEvent.InvasioneStalla inv -> {
                this.fatalError = true;
                this.integritaSistema = 0;
                System.out.println("[STALLA] *** FATAL ERROR *** Invasione: "
                        + inv.tipoInsetto() + "#" + inv.idInsetto());
                if (onFatalError != null) onFatalError.run();
            }
            case GameEvent.DannoStalla d -> applicaDanno(d.quantita());
            default -> {
                // eventi informativi non rilevanti per la stalla
            }
        }
    }

    private void applicaDanno(int danno) {
        this.integritaSistema = Math.max(0, integritaSistema - danno);
        System.out.println("[STALLA] Danno subito: " + danno + " | Integrità: " + integritaSistema);
        if (onDannoSubito != null) onDannoSubito.run();

        if (integritaSistema == 0) {
            this.fatalError = true;
            System.out.println("[STALLA] *** FATAL ERROR *** Integrità a zero.");
            if (onFatalError != null) onFatalError.run();
        }
    }

    public int     getIntegritaSistema() { return integritaSistema; }
    public boolean isFatalError()        { return fatalError; }

    public void setOnFatalError(Runnable callback)  { this.onFatalError = callback; }
    public void setOnDannoSubito(Runnable callback) { this.onDannoSubito = callback; }
}
