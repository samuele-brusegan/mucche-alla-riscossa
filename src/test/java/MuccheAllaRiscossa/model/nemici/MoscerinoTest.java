package MuccheAllaRiscossa.model.nemici;

import MuccheAllaRiscossa.model.RandomSource;
import MuccheAllaRiscossa.pattern.GameEvent;
import MuccheAllaRiscossa.pattern.Observer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Test deterministici grazie all'iniezione di {@link RandomSource}. */
class MoscerinoTest {

    private static class SpyObserver implements Observer {
        final List<GameEvent> eventi = new ArrayList<>();
        @Override public void onEvent(GameEvent e) { eventi.add(e); }
    }

    @Test
    void evadeQuandoRandomSottoSoglia() {
        // 0.0 < 0.30 → schivata garantita
        Moscerino m = new Moscerino(0, RandomSource.fixed(0.0));
        SpyObserver spy = new SpyObserver();
        m.attach(spy);

        int salutePrima = m.getSalute();
        m.subisciDanno(20);

        assertEquals(salutePrima, m.getSalute(), "schivata: nessun danno");
        assertTrue(spy.eventi.stream().anyMatch(e -> e instanceof GameEvent.MoscerinoEvade));
    }

    @Test
    void subisceDannoQuandoRandomSopraSoglia() {
        // 0.99 >= 0.30 → niente schivata, danno applicato
        Moscerino m = new Moscerino(0, RandomSource.fixed(0.99));
        SpyObserver spy = new SpyObserver();
        m.attach(spy);

        int salutePrima = m.getSalute();
        m.subisciDanno(10);

        assertEquals(salutePrima - 10, m.getSalute());
        assertTrue(spy.eventi.stream().noneMatch(e -> e instanceof GameEvent.MoscerinoEvade));
    }

    @Test
    void costruttoreDefaultUsaRandomSourceDefault() {
        // smoke test: deve esistere e funzionare senza eccezioni
        Moscerino m = new Moscerino(0);
        assertDoesNotThrow(() -> m.subisciDanno(5));
    }
}
