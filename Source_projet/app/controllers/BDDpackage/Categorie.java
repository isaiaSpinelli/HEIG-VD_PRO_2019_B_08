
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser une Catégorie de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Categorie {
    public String nom;
    public int id;
    public Color couleur;

    Categorie() {

    }

    public Categorie(int id, String nom, Color couleur){
        this.id = id;
        this.nom = nom;
        this.couleur = couleur;
    }

    public Categorie(int id, String nom){
        this(id,nom,null);
    }

    public String getName()
    {
        return nom;
    }

}
