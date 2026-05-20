package MuccheAllaRiscossa.model.gameplay;

import MuccheAllaRiscossa.model.nemici.GranTafano;
import MuccheAllaRiscossa.model.nemici.Moscone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la curva a campana di {@link OndataConfig}: si parte con un solo
 * nemico, si cresce fino al picco (W5) e poi si scende verso un finale
 * "boss-like" con pochi ma fortissimi nemici (GranTafano in W7-W8).
 */
class OndataConfigTest {

    @ParameterizedTest(name = "ondata {0}: numero coerente, insetti non vuoti, righe valide")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
    void ondataHaInsettiECoerenza(int livello) {
        OndataConfig o = OndataConfig.creaOndata(livello);
        assertEquals(livello, o.getNumeroOndata());
        assertFalse(o.getInsetti().isEmpty(), "ondata " + livello + " deve avere insetti");
        assertTrue(o.getDelayTraInsetti() > 0, "delay deve essere positivo");
        o.getInsetti().forEach(i ->
                assertTrue(i.getRiga() >= 0 && i.getRiga() < Griglia.RIGHE,
                        "riga fuori bounds nell'ondata " + livello));
    }

    @Test
    void ondata1ESoloZanzare() {
        OndataConfig o = OndataConfig.creaOndata(1);
        assertFalse(o.getInsetti().isEmpty(), "W1 non deve essere vuota");
        assertTrue(o.getInsetti().stream()
                        .allMatch(i -> i instanceof MuccheAllaRiscossa.model.nemici.Zanzara),
                "W1 e' un tutorial vivente: solo Zanzare");
    }

    @Test
    void grandeOrdaEPiuFittaDellOndataRegolare() {
        OndataConfig orda = OndataConfig.creaGrandeOrda(3);
        OndataConfig reg  = OndataConfig.creaOndata(3);
        assertTrue(orda.isGrandeOrda(), "creaGrandeOrda deve marchiare la config");
        assertFalse(reg.isGrandeOrda(),  "le ondate regolari non sono Grande Orda");
        assertTrue(orda.getDelayTraInsetti() <= reg.getDelayTraInsetti(),
                "delay piu basso → spawn piu fitto");
    }

    @Test
    void picco5HaPiuInsettiDiTutteLeAltre() {
        int sizePicco = OndataConfig.creaOndata(5).getInsetti().size();
        for (int l : new int[] {1, 2, 3, 4, 6, 7, 8}) {
            assertTrue(sizePicco >= OndataConfig.creaOndata(l).getInsetti().size(),
                    "il picco a W5 deve essere >= dell'ondata " + l);
        }
    }

    @Test
    void intensitaCresceFinoAlPicco() {
        for (int da = 1; da < 5; da++) {
            int curr = OndataConfig.creaOndata(da).getInsetti().size();
            int next = OndataConfig.creaOndata(da + 1).getInsetti().size();
            assertTrue(next >= curr,
                    "da W" + da + " a W" + (da + 1) + " gli insetti non devono calare");
        }
    }

    @Test
    void intensitaCalaDopoIlPicco() {
        int picco = OndataConfig.creaOndata(5).getInsetti().size();
        int dopo  = OndataConfig.creaOndata(6).getInsetti().size();
        int finale = OndataConfig.creaOndata(8).getInsetti().size();
        assertTrue(dopo < picco,   "dopo il picco il numero di insetti deve calare");
        assertTrue(finale < picco, "il finale ha pochi nemici, ma boss");
    }

    @Test
    void mosconeIntrodottoInOndata4() {
        assertTrue(OndataConfig.creaOndata(4).getInsetti().stream()
                        .anyMatch(i -> i instanceof Moscone),
                "il Moscone deve apparire in W4");
        assertTrue(OndataConfig.creaOndata(3).getInsetti().stream()
                        .noneMatch(i -> i instanceof Moscone),
                "in W3 il Moscone non deve esserci ancora");
    }

    @Test
    void granTafanoSoloDopoOndata6() {
        for (int l = 1; l <= 6; l++) {
            assertTrue(OndataConfig.creaOndata(l).getInsetti().stream()
                            .noneMatch(i -> i instanceof GranTafano),
                    "il GranTafano non deve apparire prima di W7 (ondata " + l + ")");
        }
        assertTrue(OndataConfig.creaOndata(7).getInsetti().stream()
                        .anyMatch(i -> i instanceof GranTafano),
                "il GranTafano deve apparire in W7");
    }

    @Test
    void ondataFinaleEDichiarata() {
        assertEquals(8, OndataConfig.ONDATA_FINALE,
                "la curva e' progettata per terminare in 8 ondate");
    }

    @Test
    void livelloOltreIlMassimoGeneraOndataBrutale() {
        OndataConfig o = OndataConfig.creaOndata(99);
        assertEquals(99, o.getNumeroOndata());
        assertEquals(Griglia.RIGHE * 2, o.getInsetti().size());
    }
}
