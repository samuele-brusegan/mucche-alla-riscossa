package MuccheAllaRiscossa.view;

import MuccheAllaRiscossa.pattern.Observer;

/**
 * StallaTorreControllo: Observer che rappresenta la stalla del giocatore.
 *
 * Riceve le notifiche dagli InsettoMutante (Subject) e gestisce la propria
 * "integrità di sistema". Se un insetto raggiunge la stalla (messaggio
 * "INVASIONE_STALLA:..."), scatta il Fatal Error → game over.
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

    /**
     * Reagisce agli eventi pubblicati dai Subject (insetti).
     *
     * Eventi gestiti:
     *  - "INVASIONE_STALLA:..." → fatal error, game over
     *  - "DANNO_STALLA:n"       → riduce integrità di n
     *  - altro                  → log a livello info
     */
    
    @Override
    public void update(String messaggio) {
        if (messaggio == null) return;

        if (messaggio.startsWith("INVASIONE_STALLA")) {
            this.fatalError = true;
            this.integritaSistema = 0;
            System.out.println("[STALLA] *** FATAL ERROR *** Invasione rilevata: " + messaggio);
            
            // Invocazione del callback per mostrare la schermata di sconfitta
            if (onFatalError != null) {
                onFatalError.run();
            }
            return;
        }

        if (messaggio.startsWith("DANNO_STALLA:")) {
            try {
                int danno = Integer.parseInt(messaggio.substring("DANNO_STALLA:".length()).trim());
                this.integritaSistema = Math.max(0, integritaSistema - danno);
                System.out.println("[STALLA] Danno subito: " + danno + " | Integrità: " + integritaSistema);
                
                // Invocazione del callback per aggiornare l'HUD della View
                if (onDannoSubito != null) {
                    onDannoSubito.run();
                }

                if (integritaSistema == 0) {
                    this.fatalError = true;
                    System.out.println("[STALLA] *** FATAL ERROR *** Integrità a zero.");
                    
                    // Invocazione del callback per il game over
                    if (onFatalError != null) {
                        onFatalError.run();
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("[STALLA] DANNO_STALLA malformato: " + message);
            }
            return;
        }

        System.out.println("[STALLA] Evento: " + messaggio);
    }
    public int     getIntegritaSistema() { return integritaSistema; }
    public boolean isFatalError()        { return fatalError; }
    public void setOnFatalError(Runnable callback) {
        this.onFatalError = callback;
    }

    public void setOnDannoSubito(Runnable callback) {
        this.onDannoSubito = callback;
    }
}
