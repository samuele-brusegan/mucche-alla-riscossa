package MuccheAllaRiscossa.integration;

import MuccheAllaRiscossa.controller.GameController;
import MuccheAllaRiscossa.model.RandomSource;
import MuccheAllaRiscossa.model.difensori.Mucca;
import MuccheAllaRiscossa.model.difensori.UnitaBovina;
import MuccheAllaRiscossa.model.difensori.Vacca;
import MuccheAllaRiscossa.model.difensori.VitellinoVedeo;
import MuccheAllaRiscossa.model.gameplay.BoassaEsplosiva;
import MuccheAllaRiscossa.model.gameplay.Proiettile;
import MuccheAllaRiscossa.model.nemici.GranTafano;
import MuccheAllaRiscossa.model.nemici.Moscerino;
import MuccheAllaRiscossa.model.nemici.Moscone;
import MuccheAllaRiscossa.pattern.GameEvent;
import MuccheAllaRiscossa.view.StallaTorreControllo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: verifica il flusso completo Subject↔Observer
 * insetto-in-raggio → mucca attacca → proiettile → insetto muore →
 * controller assegna fieno.
 *
 * Usa il vero {@link GameController} (Singleton) con reset puliti.
 */
class ObserverFlowIntegrationTest {

    private GameController gc;

    @BeforeEach
    void resetState() {
        gc = GameController.getInstance();
        gc.resetForTesting();
    }

    @Test
    void vitellinoSparaQuandoInsettoInRaggio() {
        // Schiera un Vitellino (riga 0)
        VitellinoVedeo v = new VitellinoVedeo();
        gc.schiera(v, 0, 0);

        // Aggiungi una Zanzara sulla stessa riga: viene attached ai due Subject↔Observer
        Moscone nemico = new Moscone(0);
        gc.aggiungiInsetto(nemico);

        // L'insetto pubblica "INSETTO_IN_RAGGIO" → la mucca DEVE creare un proiettile
        nemico.notifyObservers(new GameEvent.InsettoInRaggio(0, 1.0));

        Proiettile p = v.getUltimoProiettile();
        assertNotNull(p, "il vitellino deve aver creato un proiettile");
        assertTrue(p.isAttivo());
        assertEquals(0, p.getRiga());
    }

    @Test
    void flussoCompletoUcciIeAumentaFieno() {
        // Setup: vacca su riga 0, moscerino sulla stessa riga (deterministico, niente schivata)
        gc.aggiungiFieno(200); // assicura abbastanza fieno per la Vacca (costa 200)
        Vacca vacca = new Vacca();
        assertTrue(gc.schiera(vacca, 0, 0), "schieramento Vacca riuscito");
        int fienoDopoSchiera = gc.getBalleDiFieno();

        Moscerino m = new Moscerino(0, RandomSource.fixed(0.99)); // mai schivare
        gc.aggiungiInsetto(m);

        // Step 1: insetto in raggio → vacca attacca, crea proiettile
        m.notifyObservers(new GameEvent.InsettoInRaggio(0, 0.0));
        Proiettile p = vacca.getUltimoProiettile();
        assertNotNull(p);

        // Step 2: simuliamo il colpo del proiettile
        m.subisciDanno(p.getDanno());
        assertFalse(m.isVivo(), "moscerino con 25 hp morto da 40 danni");

        // Step 3: tick del controller → rimuove l'insetto morto e premia il giocatore
        gc.tick();
        assertFalse(gc.getInsetti().contains(m));
        assertEquals(fienoDopoSchiera + 25, gc.getBalleDiFieno(),
                "uccidere un insetto deve dare 25 fieno");
    }

    @Test
    void stallaRiceveInvasioneDaInsettoArrivato() {
        StallaTorreControllo stalla = gc.getStalla();
        // se è stata invasa in un test precedente la salute resta a 0; verifichiamo via flag
        // Creiamo un insetto e lo facciamo arrivare alla stalla
        Moscone m = new Moscone(0);
        gc.aggiungiInsetto(m);

        for (int i = 0; i < 200 && !m.isArrivatoAlTarget(); i++) m.muovi();
        assertTrue(m.isArrivatoAlTarget());
        assertTrue(stalla.isFatalError(), "la stalla deve essere in fatal error dopo l'invasione");
        assertTrue(gc.isGameOver());
    }

    @Test
    void boassaSulPercorsoFermaInsetto() {
        // Mucca (trappolaia) su riga 1
        Mucca beatrice = new Mucca();
        gc.schiera(beatrice, 1, 0);
        beatrice.onEvent(new GameEvent.InsettoInRaggio(1, 1.0));
        BoassaEsplosiva boassa = beatrice.getUltimaTrappola();
        assertNotNull(boassa);

        // Insetto sulla stessa riga calpesta la boassa
        Moscone vittima = new Moscone(1);
        int saluteIniziale = vittima.getSalute();

        boassa.attiva(vittima);

        assertFalse(boassa.isAttiva());
        assertTrue(vittima.getSalute() < saluteIniziale);
    }

    @Test
    void granTafanoSotterraneoIgnoraDannoMaPoiRiemerge() {
        GranTafano tafano = new GranTafano(0);
        gc.aggiungiInsetto(tafano);

        // Riduco la salute sotto il 50% per innescare lo scavo
        tafano.subisciDanno(tafano.getSaluteIniziale() / 2 + 1);
        tafano.muovi(); // entra in stato sotterraneo
        assertTrue(tafano.isSotterraneo());

        int salutePrima = tafano.getSalute();
        tafano.subisciDanno(50);
        assertEquals(salutePrima, tafano.getSalute(), "sotterraneo invulnerabile");

        // Dopo 3 tick sotto terra riemerge
        for (int i = 0; i < 3; i++) tafano.muovi();
        assertFalse(tafano.isSotterraneo());

        // Ora subisce normalmente danno
        tafano.subisciDanno(10);
        assertEquals(salutePrima - 10, tafano.getSalute());
    }

    @Test
    void detachInsettoSuArrivoEvitaNotificheAdUnita() {
        // verifica che dopo che un insetto arriva alla stalla, le mucche
        // sulla stessa riga non ricevono più aggiornamenti da quell'insetto
        UnitaBovina u = new VitellinoVedeo();
        gc.schiera(u, 0, 0);

        Moscone m = new Moscone(0);
        gc.aggiungiInsetto(m);
        for (int i = 0; i < 200 && !m.isArrivatoAlTarget(); i++) m.muovi();
        assertTrue(m.isArrivatoAlTarget());

        gc.tick(); // detach automatico

        int vitaPrima = u.getVita();
        // notifica diretta dall'insetto (ormai detached) → la mucca non riceve nulla
        m.notifyObservers(new GameEvent.Danno(50));
        assertEquals(vitaPrima, u.getVita());
    }
}
