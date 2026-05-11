
/** UnitaBovina, ovvero la Classe ASTRATTA base per tutte le mucche.*/
public abstract class UnitaBovina {
    /** Nome della pargola guerriera(vedeo, vacca, mucca...) */
    protected String nome;

    /** Quanto costa piazzarla in "Balle di Fieno" */
    protected int costoFieno;

    /** Raggio d'azione in cui il suo colpo ha effetto */
    protected double raggioAzione;

    /** Riga della griglia in cui è piazzata, sono 5 corsie (quindi da 0-4)*/
    protected int riga;

    /** Colonna della griglia in cui l'abbiamo posizionata */
    protected int colonna;

    /** Punti vita della mucca */
    protected int vita;

    /** nelle altre classi richiamo il costruttore tramite super(...)*/
    public UnitaBovina(String nome, int costoFieno, double raggioAzione, int vita) {
        this.nome         = nome;
        this.costoFieno   = costoFieno;
        this.raggioAzione = raggioAzione;
        this.vita         = vita;
    }
    /** Metodo astratto che ogni mucca avrà differente*/
    public abstract void attacca();


    /** Metodi che hanno tutte le mucche*/
    public void riceviDanno(int danno) {
        this.vita -= danno;
        if (this.vita <= 0) {
            System.out.println("[MUCCA] " + nome + " ci ha lasciato, muuuu!");
        }
    }

    /** Controlla se la mucca è ancora viva */
    public boolean isViva() {
        return this.vita > 0;
    }


    public String getNome()         { return nome; }
    public int    getCostoFieno()   { return costoFieno; }
    public double getRaggioAzione() { return raggioAzione; }
    public int    getRiga()         { return riga; }
    public int    getColonna()      { return colonna; }
    public int    getVita()         { return vita; }

    public void setRiga(int riga)         { this.riga = riga; }
    public void setColonna(int colonna)   { this.colonna = colonna; }
}
