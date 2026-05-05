```mermaid

classDiagram
    %% --- PATTERN SINGLETON ---
    class GameController {
        -static GameController instance
        -int balleDiFieno
        -int saluteStalla
        -GameController()
        +static getInstance() GameController
        +aggiungiFieno(qta: int) void
        +sottraiFieno(qta: int) void
        +getBalleDiFieno() int
    }

    %% --- PATTERN OBSERVER ---
    class Subject {
        <<interface>>
        +attach(obs: Observer) void
        +detach(obs: Observer) void
        +notifyObservers() void
    }

    class Observer {
        <<interface>>
        +update(messaggio: String) void
    }

    class StallaTorreControllo {
        -int integritaSistema
        +update(messaggio: String) void
    }

    %% --- DIFENSORI (RESISTENZA BOVINA) ---
    class UnitaBovina {
        <<abstract>>
        #String nome
        #int costoFieno
        #double raggioAzione
        +attacca() void*
    }

    class VitellinoVedeo {
        -final String arma = "Zampa di Balsa"
        +attacca() void
    }

    class MuccaBeatrice {
        -double rallentamentoBoassa
        +piazzaTrappola() void
    }

    class MuccaCornutaAssunta {
        -int dannoCarica
        +incorna() void
    }

    class MammaRosetta {
        -int velocitaLancio
        +lancioDelVitello() void
    }

    %% --- NEMICI (INSETTI MUTANTI) ---
    class InsettoMutante {
        <<abstract>>
        #int salute
        #double velocita
        #boolean arrivatoAlTarget
        +muovi() void
        +setArrivato() void
    }

    class ZanzaraElicotterista {
        -double quotaVolo
        +volaOltreBalle() void
    }

    class MosconeCorazzato {
        -int spessoreGuscioNoce
        +incassaColpo() void
    }

    class MoscerinoNinja {
        -double ampiezzaZigZag
        +evadi() void
    }

    class TafanoMinatore {
        -boolean isSotterraneo
        +scava() void
    }

    %% --- RELAZIONI ---
    %% Generalizzazioni (Ereditarietà)
    UnitaBovina <|-- VitellinoVedeo
    UnitaBovina <|-- MuccaBeatrice
    UnitaBovina <|-- MuccaCornutaAssunta
    UnitaBovina <|-- MammaRosetta

    InsettoMutante <|-- ZanzaraElicotterista
    InsettoMutante <|-- MosconeCorazzato
    InsettoMutante <|-- MoscerinoNinja
    InsettoMutante <|-- TafanoMinatore

    %% Realizzazioni (Interfacce)
    Subject <|.. InsettoMutante : implements
    Observer <|.. StallaTorreControllo : implements

    %% Associazioni e Molteplicità
    GameController "1" o-- "*" UnitaBovina : schiera >
    GameController "1" o-- "*" InsettoMutante : gestisce ondata >
    InsettoMutante "0..*" --> "1" Observer : notifica invasione >
    
    note for GameController "Singleton: Unico punto di\ncontrollo per le risorse (Fieno)"
    note for StallaTorreControllo "Observer: Se un insetto arriva,\nscatta il Fatal Error"
```
