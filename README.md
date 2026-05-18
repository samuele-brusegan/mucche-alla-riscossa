# Mucche alla Riscossa

Tower defense a tema bovino: difendi la stalla dall'invasione degli insetti mutanti schierando un esercito di mucche armate fino ai denti (anzi, alle corna).

## Trama

Una piaga di insetti mutanti minaccia la stalla. Sta a te schierare le unità bovine sulla griglia di battaglia, gestire le risorse di fieno e respingere le ondate prima che gli invasori raggiungano la torre di controllo.

## Difensori (Resistenza Bovina)

| Unità | Ruolo | Azione speciale |
|---|---|---|
| **VitellinoVedeo** | Attaccante base | Spara proiettili a corto raggio |
| **Mucca** | Trappolaia | Piazza boasse esplosive che rallentano i nemici |
| **MuccaCornuta** | Tank corpo a corpo | Incorna infliggendo danno diretto |
| **Vacca** | Artiglieria | Lancio del vitello: proiettile ad area |

## Nemici (Insetti Mutanti)

| Insetto | Caratteristica |
|---|---|
| **Zanzara** | Vola sopra le balle di fieno |
| **Moscerino** | Evasione probabilistica ai colpi |
| **Moscone** | Tank con guscio resistente |
| **GranTafano** | Scava sottoterra sotto il 50% di salute |
| **InsettoMutante** | Può essere rallentato da effetti speciali |

## Architettura

Il progetto è organizzato secondo il pattern **MVC**:

```
Cow_code/
├── Main.java
├── controller/      # GameController (Singleton)
├── view/            # StallaTorreControllo, SlideManager
├── model/
│   ├── difensori/   # Unità bovine
│   ├── nemici/      # Insetti mutanti
│   └── gameplay/    # Griglia, Proiettile, BoassaEsplosiva, OndataConfig
└── pattern/         # Interfacce Subject / Observer
```

### Design pattern usati

- **Singleton** — `GameController` come unico punto di gestione di fieno e salute della stalla.
- **Observer** — gli `InsettoMutante` (Subject) notificano la `StallaTorreControllo` (Observer) quando raggiungono il target.
- **Template Method** — le classi astratte `UnitaBovina` e `InsettoMutante` definiscono lo scheletro del comportamento.

Diagramma UML completo in [`uml.md`](./uml.md).

## Build & Run

Compilazione e avvio da linea di comando:

```bash
javac -d out $(find Cow_code -name "*.java")
java -cp out Main
```

## Struttura del repository

- `Cow_code/` — sorgenti Java
- `uml.md` — diagramma delle classi (Mermaid)
- `out/` — output di compilazione
- `LICENSE`

## Licenza

Vedi [`LICENSE`](./LICENSE).
