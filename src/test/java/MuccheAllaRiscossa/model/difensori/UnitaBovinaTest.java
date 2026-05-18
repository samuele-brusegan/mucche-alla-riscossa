package MuccheAllaRiscossa.model.difensori;

import MuccheAllaRiscossa.model.gameplay.BoassaEsplosiva;
import MuccheAllaRiscossa.model.gameplay.Proiettile;
import MuccheAllaRiscossa.pattern.GameEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitaBovinaTest {

    @Test
    void riceviDannoRiduceVita() {
        VitellinoVedeo v = new VitellinoVedeo();
        int prima = v.getVita();
        v.riceviDanno(10);
        assertEquals(prima - 10, v.getVita());
        assertEquals(StatoUnita.FERITA, v.getStato());
    }

    @Test
    void riceviDannoLetaleSegnaMorta() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.riceviDanno(9999);
        assertFalse(v.isViva());
        assertEquals(StatoUnita.MORTA, v.getStato());
    }

    @Test
    void mortaIgnoraUpdate() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.riceviDanno(9999);
        v.onEvent(new GameEvent.InsettoInRaggio(0, 0.0));
        // se fosse viva sarebbe IN_ATTACCO; resta MORTA
        assertEquals(StatoUnita.MORTA, v.getStato());
    }

    @Test
    void updateInsettoInRaggioInnescaAttacco() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.onEvent(new GameEvent.InsettoInRaggio(0, 0.0));
        assertEquals(StatoUnita.IN_ATTACCO, v.getStato());
        assertNotNull(v.getUltimoProiettile());
    }

    @Test
    void cooldownImpediscePiuAttacchiConsecutivi() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.onEvent(new GameEvent.InsettoInRaggio(0, 0.0));
        Proiettile primo = v.getUltimoProiettile();
        v.onEvent(new GameEvent.InsettoInRaggio(0, 0.0)); // cooldown attivo, non spara
        assertSame(primo, v.getUltimoProiettile());
    }

    @Test
    void tickDecrementaCooldown() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.onEvent(new GameEvent.InsettoInRaggio(0, 0.0));
        int cd = v.getCooldown();
        v.tick();
        assertEquals(cd - 1, v.getCooldown());
    }

    @Test
    void updateDannoApplicaDanno() {
        VitellinoVedeo v = new VitellinoVedeo();
        int prima = v.getVita();
        v.onEvent(new GameEvent.Danno(15));
        assertEquals(prima - 15, v.getVita());
    }

    @Test
    void eventoNonRilevanteVieneIgnorato() {
        VitellinoVedeo v = new VitellinoVedeo();
        int prima = v.getVita();
        // un evento informativo non rivolto alle unità: non deve cambiare nulla
        assertDoesNotThrow(() -> v.onEvent(new GameEvent.MoscerinoEvade(42)));
        assertEquals(prima, v.getVita());
        assertEquals(StatoUnita.ATTIVA, v.getStato());
    }

    @Test
    void onEventNullIgnorato() {
        VitellinoVedeo v = new VitellinoVedeo();
        assertDoesNotThrow(() -> v.onEvent(null));
    }

    @Test
    void finePartitaSpegneUnita() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.onEvent(new GameEvent.FinePartita());
        assertEquals(StatoUnita.MORTA, v.getStato());
    }

    @Test
    void muccaPiazzaTrappola() {
        Mucca m = new Mucca();
        m.onEvent(new GameEvent.InsettoInRaggio(0, 0.0));
        BoassaEsplosiva b = m.getUltimaTrappola();
        assertNotNull(b);
        assertTrue(b.isAttiva());
    }

    @Test
    void vaccaCreaProiettileMultiBersaglio() {
        Vacca v = new Vacca();
        v.onEvent(new GameEvent.InsettoInRaggio(0, 0.0));
        assertNotNull(v.getUltimoProiettile());
        assertTrue(v.getUltimoProiettile().isColpisceTutti());
    }

    @Test
    void idUnivociTraUnita() {
        VitellinoVedeo a = new VitellinoVedeo();
        VitellinoVedeo b = new VitellinoVedeo();
        assertNotEquals(a.getId(), b.getId());
    }
}
