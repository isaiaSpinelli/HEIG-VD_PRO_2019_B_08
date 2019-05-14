
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser un statut de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Statut {

    /**
     * nom du statut
     */
    public String nom;
    /**
     * id du statut
     */
    public int id;

    /**
     * Constructeur par défaut
     */
    Statut() {

    }

    /** Constructeur
     * @param id    : id du statut
     * @param nom   : nom du statut
     */
    public Statut(int id, String nom){
        this.id = id;
        this.nom = nom;
    }

}
