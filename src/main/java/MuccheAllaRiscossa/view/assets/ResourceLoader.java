package MuccheAllaRiscossa.view.assets;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Caricatore di sprite dal classpath con cache.
 *
 * Cerca i PNG sotto {@code /sprites/<nome>.png}; se l'asset non esiste,
 * genera un placeholder procedurale (rettangolo colorato con l'iniziale)
 * così la GUI funziona anche prima che gli asset reali siano stati aggiunti.
 *
 * Per aggiungere asset reali basta copiare i PNG in
 * {@code src/main/resources/sprites/} con i nomi attesi dalla view
 * (es. {@code mucca.png}, {@code zanzara.png}, ...).
 */
public final class ResourceLoader {

    private ResourceLoader() {}

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    /**
     * Carica lo sprite con il nome dato (senza estensione).
     * Se non esiste sul classpath, ritorna un placeholder colorato.
     *
     * @param nome     nome dello sprite (es. "mucca", "zanzara")
     * @param fallback colore del placeholder se il file manca
     * @param iniziale lettera disegnata sul placeholder
     */
    public static BufferedImage sprite(String nome, Color fallback, char iniziale) {
        return CACHE.computeIfAbsent(nome, key -> caricaOplaceholder(key, fallback, iniziale));
    }

    /** Carica un audio clip dal classpath. Ritorna null se assente. */
    public static URL audio(String nome) {
        return ResourceLoader.class.getResource("/audio/" + nome);
    }

    /* ---------- interni ---------- */

    private static BufferedImage caricaOplaceholder(String nome, Color fallback, char iniziale) {
        String path = "/sprites/" + nome + ".png";
        try (InputStream in = ResourceLoader.class.getResourceAsStream(path)) {
            if (in != null) {
                return ImageIO.read(in);
            }
        } catch (IOException ignored) {
            // cade nel placeholder
        }
        return placeholder(fallback, iniziale);
    }

    /** Genera un'immagine 64x64 con un rettangolo arrotondato e un'iniziale. */
    private static BufferedImage placeholder(Color colore, char iniziale) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(colore);
        g.fillRoundRect(2, 2, 60, 60, 16, 16);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 32));
        var fm = g.getFontMetrics();
        String s = String.valueOf(iniziale);
        int x = (64 - fm.stringWidth(s)) / 2;
        int y = (64 - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(s, x, y);
        g.dispose();
        return img;
    }
}
