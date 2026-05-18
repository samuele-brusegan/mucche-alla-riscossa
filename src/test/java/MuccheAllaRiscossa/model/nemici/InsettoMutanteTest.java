package MuccheAllaRiscossa.model.nemici;

import MuccheAllaRiscossa.pattern.GameEvent;
import MuccheAllaRiscossa.pattern.Observer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InsettoMutanteTest {

    /** Spy observer che registra tutti gli eventi ricevuti. */
    private static class SpyObserver implements Observer {
        final List<GameEvent> eventi = new ArrayList<>();
        @Override public void onEvent(GameEvent e) { eventi.add(e); }
    }

    @Test
    void muoviAvanzaSeVivo() {
        Zanzara z = new Zanzara(0);
        double prima = z.getColonna();
        z.muovi();
        assertTrue(z.getColonna() > prima);
    }

    @Test
    void mortoNonAvanza() {
        Zanzara z = new Zanzara(0);
        z.subisciDanno(9999);
        double prima = z.getColonna();
        z.muovi();
        assertEquals(prima, z.getColonna(), 0.0001);
    }

    @Test
    void notificaInvasioneStallaQuandoArriva() {
        Moscone m = new Moscone(0);
        SpyObserver spy = new SpyObserver();
        m.attach(spy);

        // Avanza fino al target
        for (int i = 0; i < 1000 && !m.isArrivatoAlTarget(); i++) m.muovi();

        assertTrue(m.isArrivatoAlTarget());
        assertTrue(spy.eventi.stream().anyMatch(e -> e instanceof GameEvent.InvasioneStalla));
    }

    @Test
    void notificaMorteQuandoSaluteAZero() {
        Moscerino m = new Moscerino(0);
        SpyObserver spy = new SpyObserver();
        m.attach(spy);

        // Tanto danno da garantire morte anche con evasione probabilistica
        for (int i = 0; i < 50 && m.isVivo(); i++) m.subisciDanno(50);

        assertFalse(m.isVivo());
        assertTrue(spy.eventi.stream().anyMatch(e -> e instanceof GameEvent.InsettoMorto));
    }

    @Test
    void detachRimuoveOsservatore() {
        Zanzara z = new Zanzara(0);
        SpyObserver spy = new SpyObserver();
        z.attach(spy);
        z.detach(spy);
        z.notifyObservers(new GameEvent.MoscerinoEvade(0));
        assertTrue(spy.eventi.isEmpty());
    }

    @Test
    void attachNullNonRompe() {
        Zanzara z = new Zanzara(0);
        assertDoesNotThrow(() -> z.attach(null));
    }

    @Test
    void rallentaRiduceVelocita() {
        Zanzara z = new Zanzara(0);
        double base = z.getVelocitaBase();
        z.rallenta(0.5);
        assertEquals(base * 0.5, z.getVelocita(), 0.0001);
    }

    @Test
    void mosconeAssorbeDanno() {
        Moscone m = new Moscone(0);
        int s = m.getSalute();
        m.subisciDanno(5); // < spessore guscio
        assertEquals(s, m.getSalute());
    }

    @Test
    void granTafanoSiSotterraSottoMetaSalute() {
        GranTafano t = new GranTafano(0);
        assertFalse(t.isSotterraneo());
        // riduco subito la salute sotto il 50%
        t.subisciDanno(t.getSaluteIniziale() / 2 + 1);
        t.muovi(); // l'avanza vede salute < meta e scava
        assertTrue(t.isSotterraneo());
    }

    @Test
    void granTafanoSotterraneoNonSubisceDanno() {
        GranTafano t = new GranTafano(0);
        t.subisciDanno(t.getSaluteIniziale() / 2 + 1);
        t.muovi();
        assertTrue(t.isSotterraneo());
        int s = t.getSalute();
        t.subisciDanno(50);
        assertEquals(s, t.getSalute());
    }
}
