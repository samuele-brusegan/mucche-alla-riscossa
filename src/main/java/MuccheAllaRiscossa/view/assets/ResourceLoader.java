package MuccheAllaRiscossa.view.assets;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Caricatore di sprite dal classpath con cache, basato su JavaFX.
 *
 * Cerca i PNG sotto {@code /sprites/<nome>.png}; se l'asset non esiste,
 * genera un placeholder procedurale (rettangolo colorato con l'iniziale)
 * così la GUI funziona anche prima che gli asset reali siano stati aggiunti.
 */
public final class ResourceLoader {

    private ResourceLoader() {}

    private static final Map<String, Image> CACHE = new HashMap<>();

    /**
     * Carica lo sprite con il nome dato (senza estensione).
     * Se non esiste sul classpath, ritorna un placeholder colorato.
     */
    public static Image sprite(String nome, Color fallback, char iniziale) {
        return CACHE.computeIfAbsent(nome, key -> caricaOplaceholder(key, fallback, iniziale));
    }

    /** Ritorna l'URL del file audio sul classpath, o {@code null} se assente. */
    public static URL audio(String nome) {
        return ResourceLoader.class.getResource("/audio/" + nome);
    }

    /* ---------- interni ---------- */

    private static Image caricaOplaceholder(String nome, Color fallback, char iniziale) {
        // 1. Prova un PNG diretto.
        try (InputStream in = ResourceLoader.class.getResourceAsStream("/sprites/" + nome + ".png")) {
            if (in != null) return new Image(in);
        } catch (Exception ignored) { /* cade nel ramo successivo */ }

        // 2. Prova un SVG: lo transcodifichiamo in PNG via Batik (size 64x64).
        try (InputStream in = ResourceLoader.class.getResourceAsStream("/sprites/" + nome + ".svg")) {
            if (in != null) return svgToImage(in, 64f, 64f);
        } catch (Exception ignored) { /* placeholder */ }

        // 3. Placeholder colorato.
        return placeholder(fallback, iniziale);
    }

    /**
     * Carica uno sprite SVG dal classpath ridimensionato a {@code w x h} px.
     * Utile per asset che vanno disegnati in formati diversi (HUD, tile, sfondo).
     */
    public static Image svgScalato(String nome, double w, double h) {
        String key = nome + "@" + (int) w + "x" + (int) h;
        return CACHE.computeIfAbsent(key, k -> {
            try (InputStream in = ResourceLoader.class.getResourceAsStream("/sprites/" + nome + ".svg")) {
                if (in != null) return svgToImage(in, (float) w, (float) h);
            } catch (Exception ignored) { /* placeholder */ }
            return placeholder(Color.MAGENTA, '?');
        });
    }

    /** Transcodifica un input stream SVG in {@link Image} JavaFX, alla dimensione data. */
    private static Image svgToImage(InputStream svg, float w, float h) throws Exception {
        PNGTranscoder t = new PNGTranscoder();
        t.addTranscodingHint(PNGTranscoder.KEY_WIDTH,  w);
        t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, h);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        t.transcode(new TranscoderInput(svg), new TranscoderOutput(out));
        return new Image(new ByteArrayInputStream(out.toByteArray()));
    }

    /**
     * Genera un'immagine 64x64 piena con il colore passato e angoli "smussati"
     * (4 pixel trasparenti nei vertici). L'iniziale non viene disegnata: per
     * il rendering testuale ci pensano gli sprite reali. È sufficiente per
     * distinguere visivamente le unità a colpo d'occhio.
     *
     * Implementato con {@link WritableImage} + {@link PixelWriter} per essere
     * thread-safe e non richiedere il JavaFX Application Thread.
     */
    private static Image placeholder(Color colore, char iniziale) {
        WritableImage img = new WritableImage(64, 64);
        PixelWriter w = img.getPixelWriter();
        Color trasparente = Color.TRANSPARENT;
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                // Smusso minimale: scava 2x2 pixel in ciascun angolo per dare un
                // accenno di "rounded corner".
                boolean angolo =
                        (x < 2 && y < 2) || (x > 61 && y < 2) ||
                        (x < 2 && y > 61) || (x > 61 && y > 61);
                w.setColor(x, y, angolo ? trasparente : colore);
            }
        }
        return img;
    }
}
