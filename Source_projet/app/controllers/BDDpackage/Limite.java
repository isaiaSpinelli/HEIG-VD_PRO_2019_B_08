
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser une Limite de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Limite {

    // public String nom;
    public int id;
    public String date;
    public double valeur;
    public Utilisateur user;
    public Recurrence recurrence ;
    public SousCategorie sousCategorie;

    Limite() {

    }

    public Limite(String date, double valeur,Utilisateur user,Recurrence recurrence,SousCategorie sousCategorie ){
        this.date = date;
        this.valeur = valeur;
        this.user = user;
        this.recurrence = recurrence;
        this.sousCategorie = sousCategorie;
    }

}
