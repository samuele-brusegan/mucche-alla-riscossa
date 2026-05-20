package MuccheAllaRiscossa.controller;

import MuccheAllaRiscossa.model.difensori.MuccaCornuta;
import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.difensori.Vacca;
import MuccheAllaRiscossa.model.difensori.VitellinoVedeo;
import MuccheAllaRiscossa.model.gameplay.Griglia;
import MuccheAllaRiscossa.model.nemici.Moscone;
import MuccheAllaRiscossa.model.nemici.Zanzara;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del Singleton {@link GameController}.
 *
 * Si trova nel package {@code MuccheAllaRiscossa.controller} per accedere al
 * metodo package-private {@code resetForTesting()} ed avere uno stato pulito
 * fra un test e l'altro (Surefire condivide la JVM tra i test della classe).
 */
class GameControllerTest {

    private GameController gc;

    @BeforeEach
    void resetState() {
        gc = GameController.getInstance();
        gc.resetForTesting();
    }

    /* ---------- Risorse: Balle di Fieno ---------- */

    @Test
    void iniziaConFienoIniziale() {
        assertEquals(GameController.FIENO_INIZIALE, gc.getBalleDiFieno());
    }

    @Test
    void aggiungiFienoIncrementa() {
        int prima = gc.getBalleDiFieno();
        gc.aggiungiFieno(25);
        assertEquals(prima + 25, gc.getBalleDiFieno());
    }

    @Test
    void sottraiFienoOkSeAbbastanza() {
        int prima = gc.getBalleDiFieno();
        assertTrue(gc.sottraiFieno(40));
        assertEquals(prima - 40, gc.getBalleDiFieno());
    }

    @Test
    void sottraiFienoFallisceSeInsufficiente() {
        int prima = gc.getBalleDiFieno();
        assertFalse(gc.sottraiFieno(prima + 1));
        assertEquals(prima, gc.getBalleDiFieno(), "fieno invariato sul fallimento");
    }

    /* ---------- Schieramento difensori ---------- */

    @Test
    void schieraScalaCostoDalFieno() {
        int prima = gc.getBalleDiFieno();
        UnitaBovina u = new VitellinoVedeo(); // costo 50
        assertTrue(gc.schiera(u, 0, 1));
        assertEquals(prima - u.getCostoFieno(), gc.getBalleDiFieno());
        assertEquals(1, gc.getUnita().size());
        assertEquals(0, u.getRiga());
        assertEquals(1, u.getColonna());
    }

    @Test
    void schieraFallisceSeFienoInsufficiente() {
        // svuoto le riserve sotto al costo di una Vacca (200)
        gc.sottraiFieno(gc.getBalleDiFieno() - 10);
        UnitaBovina u = new Vacca(); // costo 200
        assertFalse(gc.schiera(u, 0, 0));
        assertTrue(gc.getUnita().isEmpty());
    }

    @Test
    void getUnitaRitornaListaImmodificabile() {
        gc.schiera(new VitellinoVedeo(), 0, 0);
        assertThrows(UnsupportedOperationException.class,
                () -> gc.getUnita().clear());
    }

    /* ---------- Tick & ondate ---------- */

    @Test
    void tickRimuoveInsettiArrivati() {
        Zanzara z = new Zanzara(0);
        gc.aggiungiInsetto(z);
        // forza l'arrivo
        for (int n = 0; n < 100 && !z.isArrivatoAlTarget(); n++) z.muovi();
        assertTrue(z.isArrivatoAlTarget());

        gc.tick();
        assertFalse(gc.getInsetti().contains(z), "insetto arrivato deve essere rimosso");
        // l'arrivo invade la stalla → game over
        assertTrue(gc.isGameOver());
    }

    @Test
    void tickRimuoveInsettiMortiEPremiaConFieno() {
        Moscone m = new Moscone(0);
        gc.aggiungiInsetto(m);
        m.subisciDanno(9999); // ucciso
        int fienoPrima = gc.getBalleDiFieno();

        gc.tick();
        assertFalse(gc.getInsetti().contains(m));
        assertEquals(fienoPrima + GameController.FIENO_PER_KILL, gc.getBalleDiFieno(),
                "uccidere un insetto deve regalare 25 fieno");
    }

    @Test
    void pausaIniziaTrueEdAvviaOndataDopo150Tick() {
        assertTrue(gc.isInPausa());
        assertEquals(0, gc.getOndataCorrente());

        for (int i = 0; i < 150; i++) gc.tick();
        // dopo 150 tick di pausa l'ondata 1 è avviata
        assertFalse(gc.isInPausa());
        assertEquals(1, gc.getOndataCorrente());
    }

    @Test
    void generazioneRisorseAlTickConfigurato() {
        int fienoPrima = gc.getBalleDiFieno();
        for (int i = 0; i < GameController.TICK_GENERAZIONE; i++) gc.tick();
        // un solo trigger di fieno passivo atteso nei primi TICK_GENERAZIONE tick
        assertEquals(fienoPrima + GameController.FIENO_PASSIVO, gc.getBalleDiFieno());
    }

    @Test
    void aggiungiInsettoCollegaSoloUnitaSullaStessaRiga() {
        UnitaBovina sullaRigaZero = new VitellinoVedeo();
        UnitaBovina sullaRigaUno  = new MuccaCornuta();
        gc.schiera(sullaRigaZero, 0, 0);
        gc.schiera(sullaRigaUno,  1, 0);

        Zanzara nemicoRigaZero = new Zanzara(0);
        gc.aggiungiInsetto(nemicoRigaZero);

        // Un evento di danno notificato deve toccare solo la mucca della riga 0
        int vitaPrima0 = sullaRigaZero.getVita();
        int vitaPrima1 = sullaRigaUno.getVita();
        nemicoRigaZero.notifyObservers(new MuccheAllaRiscossa.pattern.GameEvent.Danno(10));

        assertEquals(vitaPrima0 - 10, sullaRigaZero.getVita());
        assertEquals(vitaPrima1, sullaRigaUno.getVita(), "altra riga non viene toccata");
    }

    @Test
    void calcolaDistanzaEuclidea() {
        UnitaBovina u = new VitellinoVedeo();
        u.setRiga(0); u.setColonna(0);
        Zanzara z = new Zanzara(0);
        // z parte a colonna 0, riga 0 → distanza 0
        assertEquals(0.0, gc.calcolaDistanza(u, z), 0.0001);
    }

    @Test
    void grigliaHaDimensioniAttese() {
        // sanity check delle costanti viste dal controller
        assertEquals(5, Griglia.RIGHE);
        assertEquals(10, Griglia.COLONNE);
    }
}
