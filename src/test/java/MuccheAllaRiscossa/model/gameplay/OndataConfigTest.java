package MuccheAllaRiscossa.model.gameplay;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OndataConfigTest {

    @Test
    void ondata1HaDelayCoerente() {
        OndataConfig o = OndataConfig.creaOndata(1);
        assertEquals(1, o.getNumeroOndata());
        assertFalse(o.getInsetti().isEmpty());
        assertTrue(o.getDelayTraInsetti() > 0);
    }

    @Test
    void difficoltaCresceConIlLivello() {
        OndataConfig o1 = OndataConfig.creaOndata(1);
        OndataConfig o5 = OndataConfig.creaOndata(5);
        assertTrue(o5.getInsetti().size() >= o1.getInsetti().size());
        assertTrue(o5.getDelayTraInsetti() <= o1.getDelayTraInsetti());
    }

    @Test
    void livelloOltreIlMassimoGeneraOndataBrutale() {
        OndataConfig o = OndataConfig.creaOndata(99);
        assertEquals(99, o.getNumeroOndata());
        assertEquals(Griglia.RIGHE * 2, o.getInsetti().size());
    }
}
