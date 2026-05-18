package MuccheAllaRiscossa.model.difensori;

import MuccheAllaRiscossa.model.gameplay.BoassaEsplosiva;
import MuccheAllaRiscossa.model.gameplay.Proiettile;
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
        v.update("INSETTO_IN_RAGGIO");
        // se fosse viva sarebbe IN_ATTACCO; resta MORTA
        assertEquals(StatoUnita.MORTA, v.getStato());
    }

    @Test
    void updateInsettoInRaggioInnescaAttacco() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.update("INSETTO_IN_RAGGIO");
        assertEquals(StatoUnita.IN_ATTACCO, v.getStato());
        assertNotNull(v.getUltimoProiettile());
    }

    @Test
    void cooldownImpediscePiuAttacchiConsecutivi() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.update("INSETTO_IN_RAGGIO");
        Proiettile primo = v.getUltimoProiettile();
        v.update("INSETTO_IN_RAGGIO"); // cooldown attivo, non spara
        assertSame(primo, v.getUltimoProiettile());
    }

    @Test
    void tickDecrementaCooldown() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.update("INSETTO_IN_RAGGIO");
        int cd = v.getCooldown();
        v.tick();
        assertEquals(cd - 1, v.getCooldown());
    }

    @Test
    void updateDannoApplicaDanno() {
        VitellinoVedeo v = new VitellinoVedeo();
        int prima = v.getVita();
        v.update("DANNO:15");
        assertEquals(prima - 15, v.getVita());
    }

    @Test
    void updateDannoMalformatoNonRompe() {
        VitellinoVedeo v = new VitellinoVedeo();
        int prima = v.getVita();
        assertDoesNotThrow(() -> v.update("DANNO:non-un-numero"));
        assertEquals(prima, v.getVita());
    }

    @Test
    void updateNullIgnorato() {
        VitellinoVedeo v = new VitellinoVedeo();
        assertDoesNotThrow(() -> v.update(null));
    }

    @Test
    void finePartitaSpegneUnita() {
        VitellinoVedeo v = new VitellinoVedeo();
        v.update("FINE_PARTITA");
        assertEquals(StatoUnita.MORTA, v.getStato());
    }

    @Test
    void muccaPiazzaTrappola() {
        Mucca m = new Mucca();
        m.update("INSETTO_IN_RAGGIO");
        BoassaEsplosiva b = m.getUltimaTrappola();
        assertNotNull(b);
        assertTrue(b.isAttiva());
    }

    @Test
    void vaccaCreaProiettileMultiBersaglio() {
        Vacca v = new Vacca();
        v.update("INSETTO_IN_RAGGIO");
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
