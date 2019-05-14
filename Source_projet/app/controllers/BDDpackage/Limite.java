
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
    /**
     * ID de la limite
     */
    public int id;
    /**
     * date de la limite
     */
    public String date;
    /**
     * valeur du montant de la limite
     */
    public double valeur;
    /**
     * Utilisateur liée à la limite
     */
    public Utilisateur user;
    /**
     * Recurrence de la limite
     */
    public Recurrence recurrence ;
    /**
     * Sous catégorie liée à la limite
     */
    public SousCategorie sousCategorie;

    Limite() {

    }

    /** Constructeur
     * @param date          : date de la limite
     * @param valeur        : valeur du montant de la limite
     * @param user          : utilisateur liée à la limite
     * @param recurrence    : recurrence de la limite
     * @param sousCategorie : sous catégorie liée à la limite
     */
    public Limite(String date, double valeur,Utilisateur user,Recurrence recurrence,SousCategorie sousCategorie ){
        this.date = date;
        this.valeur = valeur;
        this.user = user;
        this.recurrence = recurrence;
        this.sousCategorie = sousCategorie;
    }

}
