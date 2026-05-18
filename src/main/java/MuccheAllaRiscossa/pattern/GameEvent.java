package MuccheAllaRiscossa.pattern;

/**
 * Tutti gli eventi che possono viaggiare tra Subject e Observer.
 *
 * Sealed + record: il compilatore garantisce esaustività e immutabilità,
 * eliminando il vecchio protocollo a stringhe ("DANNO:50", "INSETTO_IN_RAGGIO").
 *
 * Vantaggi rispetto a {@code String}:
 * - tipi sicuri, niente {@code parseInt} con try/catch a runtime
 * - pattern matching esaustivo nello {@code switch}
 * - facile estendere senza rompere clienti esistenti (basta aggiungere un nuovo
 *   permesso e gestirlo dove serve; gli altri lo ignorano per default).
 */
public sealed interface GameEvent {

    /** Un insetto è entrato nel raggio d'azione di una mucca. */
    record InsettoInRaggio(int rigaInsetto, double colonnaInsetto) implements GameEvent {}

    /** Un'unità (mucca o stalla) subisce danno. */
    record Danno(int quantita) implements GameEvent {}

    /** L'unità deve smettere di reagire (fine partita). */
    record FinePartita() implements GameEvent {}

    /** Un insetto ha raggiunto la stalla. */
    record InvasioneStalla(String tipoInsetto, int idInsetto) implements GameEvent {}

    /** Un insetto è stato eliminato. */
    record InsettoMorto(String tipoInsetto, int idInsetto) implements GameEvent {}

    /** La stalla subisce danno diretto. */
    record DannoStalla(int quantita) implements GameEvent {}

    /** Il GranTafano cambia stato di scavo. */
    record TafanoScava(int idInsetto, boolean sotterraneo) implements GameEvent {}

    /** La zanzara prende quota. */
    record ZanzaraInVolo(int idInsetto, int riga) implements GameEvent {}

    /** Il moscerino schiva un colpo. */
    record MoscerinoEvade(int idInsetto) implements GameEvent {}
}
