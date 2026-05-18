package MuccheAllaRiscossa.pattern;

/**
 * Interfaccia Observer
 *
 * Chi "osserva" gli eventi di gioco deve implementare questa interfaccia.
 * In pratica: la StallaTorreControllo la implementa per sapere
 * quando un insetto è arrivato alla stalla; allo stesso modo le UnitaBovina
 * la implementano per reagire (es. attaccare) quando un insetto entra
 * nel loro raggio d'azione.
 *
 * Il metodo update() viene chiamato automaticamente dal Subject
 * ogni volta che succede qualcosa di importante (es. GranTafano arriva
 * alla nostra stalla, oppure un insetto entra nella corsia di una mucca).
 *
 * Il messaggio è una stringa con un protocollo a prefissi concordato
 * tra Subject e Observer (es. "INSETTO_IN_RAGGIO:riga:colonna",
 * "DANNO:50", "FINE_PARTITA"). Mantenere il protocollo a stringhe
 * coerente con il diagramma UML del progetto.
 */
public interface Observer {

    /**
     * Notifica l'Observer di un evento del Subject.
     *
     * @param messaggio descrizione dell'evento (protocollo a prefissi).
     */
    void update(String messaggio);
}
