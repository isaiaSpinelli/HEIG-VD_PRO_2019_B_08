
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser un statut de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Statut {

    public String nom;
    public int id;

    Statut() {

    }

    public Statut(int id, String nom){
        this.id = id;
        this.nom = nom;
    }

}
