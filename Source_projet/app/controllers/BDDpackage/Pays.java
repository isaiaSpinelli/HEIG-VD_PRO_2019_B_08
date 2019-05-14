
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser un Pays de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Pays {

    public String nom;
    public int id;

    Pays() {

    }

    public Pays(int id, String nom){
        this.id = id;
        this.nom = nom;
    }

}
