package MuccheAllaRiscossa.model.gameplay;

import MuccheAllaRiscossa.model.nemici.GranTafano;
import MuccheAllaRiscossa.model.nemici.Moscone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class OndataConfigTest {

    @ParameterizedTest(name = "ondata {0} ha numero {0} e insetti non vuoti")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void ondataHaInsettiECoerenza(int livello) {
        OndataConfig o = OndataConfig.creaOndata(livello);
        assertEquals(livello, o.getNumeroOndata());
        assertFalse(o.getInsetti().isEmpty(), "ondata " + livello + " deve avere insetti");
        assertTrue(o.getDelayTraInsetti() > 0, "delay deve essere positivo");
        // ogni insetto deve essere su una riga valida
        o.getInsetti().forEach(i -> {
            assertTrue(i.getRiga() >= 0 && i.getRiga() < Griglia.RIGHE,
                    "riga fuori bounds nell'ondata " + livello);
        });
    }

    @ParameterizedTest(name = "dall'ondata {0} a {1} la difficoltà non cala")
    @CsvSource({"1,2", "2,3", "3,4", "4,5"})
    void difficoltaMonotonicamenteCrescente(int da, int a) {
        OndataConfig o1 = OndataConfig.creaOndata(da);
        OndataConfig o2 = OndataConfig.creaOndata(a);
        assertTrue(o2.getInsetti().size() >= o1.getInsetti().size(),
                "ondata " + a + " deve avere >= insetti dell'ondata " + da);
        assertTrue(o2.getDelayTraInsetti() <= o1.getDelayTraInsetti(),
                "delay ondata " + a + " deve essere <= ondata " + da);
    }

    @Test
    void ondata5ContieneIlBoss() {
        OndataConfig o = OndataConfig.creaOndata(5);
        assertTrue(o.getInsetti().stream().anyMatch(i -> i instanceof GranTafano),
                "l'ondata finale deve avere almeno un GranTafano");
    }

    @Test
    void ondata3IntroduceMoscone() {
        OndataConfig o = OndataConfig.creaOndata(3);
        assertTrue(o.getInsetti().stream().anyMatch(i -> i instanceof Moscone));
    }

    @Test
    void livelloOltreIlMassimoGeneraOndataBrutale() {
        OndataConfig o = OndataConfig.creaOndata(99);
        assertEquals(99, o.getNumeroOndata());
        assertEquals(Griglia.RIGHE * 2, o.getInsetti().size());
    }
}
