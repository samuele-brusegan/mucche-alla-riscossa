package MuccheAllaRiscossa.model.gameplay;

import MuccheAllaRiscossa.model.difensori.VitellinoVedeo;
import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrigliaTest {

    @Test
    void cellaIniziaLibera() {
        Griglia g = new Griglia();
        assertTrue(g.isCellaLibera(0, 0));
        assertTrue(g.isCellaLibera(4, 9));
    }

    @Test
    void occupaECellaNonPiuLibera() {
        Griglia g = new Griglia();
        UnitaBovina u = new VitellinoVedeo();
        g.occupa(2, 3, u);
        assertFalse(g.isCellaLibera(2, 3));
        assertSame(u, g.getUnitaInCella(2, 3));
    }

    @Test
    void liberaRipristinaCella() {
        Griglia g = new Griglia();
        g.occupa(1, 1, new VitellinoVedeo());
        g.libera(1, 1);
        assertTrue(g.isCellaLibera(1, 1));
        assertNull(g.getUnitaInCella(1, 1));
    }

    @Test
    void coordinateFuoriBoundsNonSonoLibere() {
        Griglia g = new Griglia();
        assertFalse(g.isCellaLibera(-1, 0));
        assertFalse(g.isCellaLibera(0, -1));
        assertFalse(g.isCellaLibera(Griglia.RIGHE, 0));
        assertFalse(g.isCellaLibera(0, Griglia.COLONNE));
    }

    @Test
    void getUnitaFuoriBoundsRitornaNull() {
        Griglia g = new Griglia();
        assertNull(g.getUnitaInCella(-1, 0));
        assertNull(g.getUnitaInCella(0, 99));
    }

    @Test
    void occupaFuoriBoundsNonRompe() {
        Griglia g = new Griglia();
        assertDoesNotThrow(() -> g.occupa(-1, -1, new VitellinoVedeo()));
        assertDoesNotThrow(() -> g.libera(99, 99));
    }
}
