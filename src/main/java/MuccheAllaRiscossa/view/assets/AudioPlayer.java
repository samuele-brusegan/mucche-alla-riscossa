package MuccheAllaRiscossa.view.assets;

import javafx.scene.media.AudioClip;

import java.net.URL;

/**
 * Riproduzione audio fire-and-forget per effetti sonori brevi, basata su JavaFX.
 *
 * Cerca i file in {@code /audio/<nome>.wav}. Se l'asset manca o l'audio
 * non è disponibile (headless / sistema senza scheda), il metodo non fa
 * nulla e non lancia eccezioni — così la mancanza degli asset non blocca
 * il gioco.
 */
public final class AudioPlayer {

    private AudioPlayer() {}

    /** Riproduce una clip una volta, in modo non bloccante. */
    public static void play(String nomeFileWav) {
        URL url = ResourceLoader.audio(nomeFileWav);
        if (url == null) return; // asset assente: silenzio
        try {
            AudioClip clip = new AudioClip(url.toExternalForm());
            clip.play();
        } catch (Exception e) {
            // fallback silenzioso: niente audio non deve mai rompere la partita
            System.err.println("[AudioPlayer] impossibile riprodurre " + nomeFileWav + ": " + e.getMessage());
        }
    }
}
