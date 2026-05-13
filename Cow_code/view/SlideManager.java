package MuccheAllaRiscossa.view;

/**
 * SlideManager: gestisce le schermate del gioco (menu, partita, game over).
 *
 * Stub minimale: l'implementazione concreta dell'interfaccia grafica
 * spetta al team view.
 */
public class SlideManager {

    /** Possibili schermate. */
    public enum Slide { MENU, PARTITA, PAUSA, GAME_OVER, VITTORIA }

    private Slide slideCorrente;

    public SlideManager() {
        this.slideCorrente = Slide.MENU;
    }

    /** Cambia la schermata corrente. */
    public void mostra(Slide slide) {
        System.out.println("[SLIDE] " + slideCorrente + " -> " + slide);
        this.slideCorrente = slide;
    }

    public Slide getSlideCorrente() { return slideCorrente; }
}
