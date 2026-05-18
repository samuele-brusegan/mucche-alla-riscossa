package MuccheAllaRiscossa.view.assets;

import MuccheAllaRiscossa.model.difensori.MuccaCornuta;
import MuccheAllaRiscossa.model.nemici.GranTafano;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class ResourceLoaderTest {

    @Test
    void spriteAssenteRitornaPlaceholder64x64() {
        BufferedImage img = ResourceLoader.sprite("nome-che-non-esiste-mai", Color.RED, 'X');
        assertNotNull(img);
        assertEquals(64, img.getWidth());
        assertEquals(64, img.getHeight());
    }

    @Test
    void cacheRestituisceLaStessaIstanza() {
        BufferedImage a = ResourceLoader.sprite("cache-test", Color.BLUE, 'A');
        BufferedImage b = ResourceLoader.sprite("cache-test", Color.RED, 'Z');
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
