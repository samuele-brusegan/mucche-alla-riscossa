package MuccheAllaRiscossa.pattern;

/**
 * Interfaccia Subject del pattern Observer.
 *
 * Chi pubblica eventi a cui altri oggetti vogliono reagire deve
 * implementare questa interfaccia. Nel nostro gioco la implementano
 * gli InsettoMutante (Subject) che notificano StallaTorreControllo e
 * UnitaBovina (Observer) ogni volta che succede qualcosa di rilevante
 * (es. invasione della stalla, ingresso in raggio di una mucca).
 */
public interface Subject {

    /**
     * Registra un osservatore che riceverà le notifiche.
     *
     * @param obs osservatore da registrare (non null).
     */
    void attach(Observer obs);

    /**
     * Rimuove un osservatore precedentemente registrato. No-op se assente.
     *
     * @param obs osservatore da rimuovere.
     */
    void detach(Observer obs);

    /**
     * Notifica tutti gli osservatori registrati con il messaggio dato.
     *
     * @param messaggio descrizione dell'evento (vedi protocollo in Observer).
     */
    void notifyObservers(String messaggio);
}
