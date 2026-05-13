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
            return;
        }

        if (messaggio.startsWith("DANNO_STALLA:")) {
            try {
                int danno = Integer.parseInt(messaggio.substring("DANNO_STALLA:".length()).trim());
                this.integritaSistema = Math.max(0, integritaSistema - danno);
                System.out.println("[STALLA] Danno subito: " + danno + " | Integrità: " + integritaSistema);
                if (integritaSistema == 0) {
                    this.fatalError = true;
                    System.out.println("[STALLA] *** FATAL ERROR *** Integrità a zero.");
                }
            } catch (NumberFormatException e) {
                System.err.println("[STALLA] DANNO_STALLA malformato: " + messaggio);
            }
            return;
        }

        System.out.println("[STALLA] Evento: " + messaggio);
    }

    public int     getIntegritaSistema() { return integritaSistema; }
    public boolean isFatalError()        { return fatalError; }
}
