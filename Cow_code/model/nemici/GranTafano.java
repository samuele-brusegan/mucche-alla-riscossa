package MuccheAllaRiscossa.model.nemici;

/**
 * GranTafano: il boss. Quando entra in difficoltà si sotterra e
 * riemerge più avanti, scavalcando una porzione della corsia.
 */
public class GranTafano extends InsettoMutante {

    /** True mentre l'insetto è in fase di scavo (invulnerabile, invisibile). */
    private boolean isSotterraneo;

    public GranTafano(int riga) {
        super(400, 0.3, riga);
        this.isSotterraneo = false;
    }

    /** Inizia/termina la fase di scavo, notifica il cambio di stato. */
    public void scava() {
        this.isSotterraneo = !isSotterraneo;
        System.out.println("[GranTafano#" + id + "] " + (isSotterraneo ? "Si sotterra!" : "Riemerge!"));
        notifyObservers("TAFANO_SCAVA:" + id + ":sotterraneo=" + isSotterraneo);
    }

    /** Mentre è sotterraneo non subisce danno. */
    @Override
    public void subisciDanno(int danno) {
        if (isSotterraneo) {
            System.out.println("[GranTafano#" + id + "] Sotterraneo, il colpo manca!");
            return;
        }
        super.subisciDanno(danno);
    }

    public boolean isSotterraneo() { return isSotterraneo; }
}
