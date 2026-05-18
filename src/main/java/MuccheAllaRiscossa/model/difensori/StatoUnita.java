package MuccheAllaRiscossa.model.difensori;

/**
 * Stato logico di una UnitaBovina sul campo.
 *
 * - ATTIVA:     piazzata e pronta, in attesa di stimoli.
 * - IN_ATTACCO: sta eseguendo (o ha appena eseguito) il suo attacco.
 * - FERITA:     ha subito danno ma è ancora viva (vita > 0).
 * - MORTA:      vita <= 0, non reagisce più alle notifiche.
 */
public enum StatoUnita {
    ATTIVA,
    IN_ATTACCO,
    FERITA,
    MORTA
}
