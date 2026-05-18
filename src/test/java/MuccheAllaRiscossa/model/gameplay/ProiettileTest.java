package MuccheAllaRiscossa.model.gameplay;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProiettileTest {

    @Test
    void avanzaIncrementaColonna() {
        Proiettile p = new Proiettile(10, 1.0, 2, 0.0, false);
        p.muovi();
        assertEquals(1.0, p.getColonna(), 0.0001);
        assertTrue(p.isAttivo());
    }

    @Test
    void siDisattivaUscendoDallaGriglia() {
        Proiettile p = new Proiettile(10, Griglia.COLONNE + 1, 0, 0.0, false);
        p.muovi();
        assertFalse(p.isAttivo());
    }

    @Test
    void colpoSingoloSiDisattivaDopoImpatto() {
        Proiettile p = new Proiettile(10, 1.0, 0, 0.0, false);
        p.segnaColpito();
        assertFalse(p.isAttivo());
    }

    @Test
    void colpiscePiuBersagliRestaAttivo() {
        Proiettile p = new Proiettile(10, 1.0, 0, 0.0, true);
        p.segnaColpito();
        assertTrue(p.isAttivo());
    }

    @Test
    void proiettileInattivoNonAvanza() {
        Proiettile p = new Proiettile(10, 1.0, 0, 0.0, false);
        p.setAttivo(false);
        p.muovi();
        assertEquals(0.0, p.getColonna(), 0.0001);
    }
}
