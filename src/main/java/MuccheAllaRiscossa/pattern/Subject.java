package MuccheAllaRiscossa.pattern;

/**
 * Interfaccia Subject del pattern Observer.
 *
 * Pubblica {@link GameEvent} tipizzati ai propri Observer registrati.
 * Nel gioco la implementano gli {@code InsettoMutante}, che notificano
 * stalla e mucche di movimento, danni e morte.
 */
public interface Subject {

    /** Registra un osservatore (no-op se null o già presente). */
    void attach(Observer obs);

    /** Rimuove un osservatore (no-op se assente). */
    void detach(Observer obs);

    /** Notifica tutti gli osservatori con l'evento dato. */
    void notifyObservers(GameEvent evento);
}
