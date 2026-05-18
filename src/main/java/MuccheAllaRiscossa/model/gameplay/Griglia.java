package MuccheAllaRiscossa.model.gameplay;

import MuccheAllaRiscossa.model.difensori.UnitaBovina;

/**
 * Griglia di gioco 5x10 su cui vengono piazzate le unita bovine.
 *
 * Tiene traccia di quali celle sono occupate e da chi, cosi il
 * controller puo verificare velocemente se una posizione e libera
 * prima di schierare un difensore.
 */
public class Griglia {

    public static final int RIGHE   = 5;
    public static final int COLONNE = 10;

    /** Matrice delle unita piazzate; null = cella libera */
    private final UnitaBovina[][] celle;

    public Griglia() {
        this.celle = new UnitaBovina[RIGHE][COLONNE];
    }

    /** Controlla se la cella indicata e libera (nessuna unita piazzata) */
    public boolean isCellaLibera(int riga, int colonna) {
        if (!inBounds(riga, colonna)) return false;
        return celle[riga][colonna] == null;
    }

    /** Segna la cella come occupata dall'unita data */
    public void occupa(int riga, int colonna, UnitaBovina unita) {
        if (!inBounds(riga, colonna)) return;
        celle[riga][colonna] = unita;
    }

    /** Libera una cella (es. quando l'unita muore) */
    public void libera(int riga, int colonna) {
        if (!inBounds(riga, colonna)) return;
        celle[riga][colonna] = null;
    }

    /** Restituisce l'unita piazzata in una cella, oppure null se vuota */
    public UnitaBovina getUnitaInCella(int riga, int colonna) {
        if (!inBounds(riga, colonna)) return null;
        return celle[riga][colonna];
    }

    /** Verifica che le coordinate siano dentro i limiti della griglia */
    private boolean inBounds(int riga, int colonna) {
        return riga >= 0 && riga < RIGHE && colonna >= 0 && colonna < COLONNE;
    }
}
