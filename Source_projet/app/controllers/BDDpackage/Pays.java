
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser un Pays de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Pays {

    /**
     * nom du pays
     */
    public String nom;
    /**
     * ID du pays
     */
    public int id;

    Pays() {

    }

    /** Constructeur
     * @param id    : ID du pays
     * @param nom   : nom du pays
     */
    public Pays(int id, String nom){
        this.id = id;
        this.nom = nom;
    }

}
