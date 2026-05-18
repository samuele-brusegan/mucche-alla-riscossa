package MuccheAllaRiscossa.view;

/**
 * SlideManager: gestisce le schermate del gioco (menu, partita, game over)
 * collegandosi direttamente alla GameWindow reale tramite CardLayout.
 */
public class SlideManager {

    /** Possibili schermate. */
    public enum Slide { MENU, PARTITA, PAUSA, GAME_OVER, VITTORIA }

    private static SlideManager instance;
    private GameWindow window;
    private Slide slideCorrente;

    private SlideManager() {
        this.slideCorrente = Slide.MENU;
    }

    /**
     * Ritorna l'istanza globale e centralizzata dello SlideManager (Singleton).
     */
    public static synchronized SlideManager getInstance() {
        if (instance == null) {
            instance = new SlideManager();
        }
        return instance;
    }

    /**
     * Collega la finestra principale al manager delle slide per effettuare i cambi di pannello.
     */
    public void setWindow(GameWindow window) {
        this.window = window;
    }

    /** * Cambia la schermata corrente effettuando lo switch reale nel CardLayout.
     */
    public void mostra(Slide slide) {
        System.out.println("[SLIDE] " + slideCorrente + " -> " + slide);
        this.slideCorrente = slide;

        if (window == null) {
            System.err.println("[SLIDE] Errore: GameWindow non ancora agganciata allo SlideManager.");
            return;
        }

        // Effettuiamo lo switch reale in base alla costante passata
        switch (slide) {
            case MENU:
                window.mostraPannello("MENU");
                break;
            case PARTITA:
                window.mostraPannello("GAME");
                break;
            case GAME_OVER:
                window.mostraPannello("GAME_OVER");
                break;
            case VITTORIA:
                window.mostraPannello("VITTORIA");
                break;
            case PAUSA:
                // TODO: Gestione eventuale pannello di pausa se implementato in futuro
                break;
        }
    }

    public Slide getSlideCorrente() { return slideCorrente; }
}
