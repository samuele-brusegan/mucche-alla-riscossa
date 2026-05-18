package MuccheAllaRiscossa.model.gameplay;

import MuccheAllaRiscossa.model.nemici.Zanzara;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoassaEsplosivaTest {

    @Test
    void attivaInfliggeDannoEDisattiva() {
        BoassaEsplosiva b = new BoassaEsplosiva(20, 0.3, 0, 1);
        Zanzara z = new Zanzara(0);
        int saluteIniziale = z.getSalute();

        b.attiva(z);

        assertFalse(b.isAttiva());
        assertTrue(z.getSalute() < saluteIniziale);
    }

    @Test
    void attivaSuNullNonRompe() {
        BoassaEsplosiva b = new BoassaEsplosiva(20, 0.3, 0, 1);
        assertDoesNotThrow(() -> b.attiva(null));
        assertTrue(b.isAttiva());
    }

    @Test
    void boassaGiaConsumataNonRiattiva() {
        BoassaEsplosiva b = new BoassaEsplosiva(20, 0.3, 0, 1);
        Zanzara z1 = new Zanzara(0);
        b.attiva(z1);

        Zanzara z2 = new Zanzara(0);
        int salutePrima = z2.getSalute();
        b.attiva(z2);
        assertEquals(salutePrima, z2.getSalute());
    }
}
