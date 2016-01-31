package model;

/**
 * Created by Fabrice on 7/30/2015.
 */
public class Alarms {

    private String bandaId;
    private String bandaTotal;
    private String bandeName;
    private String[] numero;
    private String[] couleur;

    public Alarms(String bandaId,String bandaTotal,String[] numero,String[] couleur,String bandeName){
        setBandaId(bandaId);
        setBandaTotal(bandaTotal);
        setNumero(numero);
        setCouleur(couleur);
        setBandeName(bandeName);
    }


    public String getBandeName() {
        return bandeName;
    }

    public void setBandeName(String bandeName) {
        this.bandeName = bandeName;
    }

    public String[] getNumero() {
        return numero;
    }

    public void setNumero(String[] numero) {
        this.numero = numero;
    }

    public String getBandaId() {
        return bandaId;
    }

    public void setBandaId(String bandaId) {
        this.bandaId = bandaId;
    }

    public String getBandaTotal() {
        return bandaTotal;
    }

    public void setBandaTotal(String bandaTotal) {
        this.bandaTotal = bandaTotal;
    }

    public String[] getCouleur() {
        return couleur;
    }

    public void setCouleur(String[] couleur) {
        this.couleur = couleur;
    }
}
