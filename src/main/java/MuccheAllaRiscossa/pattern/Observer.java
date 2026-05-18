package MuccheAllaRiscossa.pattern;

/**
 * Interfaccia Observer.
 *
 * Riceve {@link GameEvent} tipizzati dai Subject. Le implementazioni sono
 * tipicamente la {@code StallaTorreControllo} (per la salute della stalla)
 * e le {@code UnitaBovina} (per reagire ai nemici in raggio).
 *
 * Le implementazioni dovrebbero usare un {@code switch} su pattern di
 * {@code GameEvent} e ignorare gli eventi non rilevanti, così il protocollo
 * resta estensibile senza rompere i client.
 */
public interface Observer {

    /**
     * Notifica l'Observer di un evento del Subject.
     *
     * @param evento descrizione tipizzata dell'evento (non null).
     */
    void onEvent(GameEvent evento);
}
