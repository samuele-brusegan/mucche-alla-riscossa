package MuccheAllaRiscossa.view.assets;

import MuccheAllaRiscossa.model.difensori.MuccaCornuta;
import MuccheAllaRiscossa.model.nemici.GranTafano;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ResourceLoaderTest {

    @BeforeAll
    static void avviaJavaFx() throws Exception {
        // Inizializza il toolkit JavaFX una sola volta (Monocle in CI headless).
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(5, TimeUnit.SECONDS);
        } catch (IllegalStateException alreadyStarted) {
            // toolkit già attivo: ok
        }
    }

    @Test
    void spriteAssenteRitornaPlaceholder64x64() {
        Image img = ResourceLoader.sprite("nome-che-non-esiste-mai", Color.RED, 'X');
        assertNotNull(img);
        assertEquals(64, (int) img.getWidth());
        assertEquals(64, (int) img.getHeight());
    }

    @Test
    void cacheRestituisceLaStessaIstanza() {
        Image a = ResourceLoader.sprite("cache-test", Color.BLUE, 'A');
        Image b = ResourceLoader.sprite("cache-test", Color.RED, 'Z');
        assertSame(a, b, "stessa chiave → stesso oggetto in cache");
    }

    @Test
    void audioAssenteRitornaNull() {
        assertNull(ResourceLoader.audio("non-esiste.wav"));
    }

    @Test
    void spritesMappingDifensoriENemiciNonNull() {
        assertNotNull(Sprites.of(new MuccaCornuta()));
        assertNotNull(Sprites.of(new GranTafano(0)));
    }
}
