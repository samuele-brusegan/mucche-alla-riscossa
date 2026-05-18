package MuccheAllaRiscossa.model;

import java.util.random.RandomGenerator;

/**
 * Sorgente di numeri casuali iniettabile nel model.
 *
 * Estraendo questa dipendenza dai punti dove serve aleatorietà
 * (es. evasione del Moscerino) i test possono iniettare implementazioni
 * deterministiche e verificare entrambi i rami di una decisione random.
 */
@FunctionalInterface
public interface RandomSource {

    /** Ritorna un double uniforme in [0.0, 1.0). */
    double nextDouble();

    /** Sorgente di default basata su {@link RandomGenerator#getDefault()}. */
    RandomSource DEFAULT = RandomGenerator.getDefault()::nextDouble;

    /** Ritorna sempre lo stesso valore: utile nei test. */
    static RandomSource fixed(double value) {
        return () -> value;
    }
}
