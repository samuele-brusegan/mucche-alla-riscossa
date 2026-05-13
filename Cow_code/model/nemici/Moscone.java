package MuccheAllaRiscossa.model.nemici;

/**
 * Moscone: insetto pesante e corazzato. Lento ma incassa molti colpi
 * grazie al guscio di noce che riduce parte del danno ricevuto.
 */
public class Moscone extends InsettoMutante {

    /** Spessore del guscio: danno assorbito per ogni colpo. */
    private int spessoreGuscioNoce;

    public Moscone(int riga) {
        super(200, 0.2, riga);
        this.spessoreGuscioNoce = 10;
    }

    /** Riduce il danno in arrivo grazie al guscio. */
    public void incassaColpo() {
        System.out.println("[Moscone#" + id + "] Guscio di noce assorbe " + spessoreGuscioNoce + " danni.");
    }

    /** Override: il guscio assorbe parte del danno prima di applicarlo. */
    @Override
    public void subisciDanno(int danno) {
        incassaColpo();
        int effettivo = Math.max(0, danno - spessoreGuscioNoce);
        super.subisciDanno(effettivo);
    }

    public int getSpessoreGuscioNoce() { return spessoreGuscioNoce; }
}
