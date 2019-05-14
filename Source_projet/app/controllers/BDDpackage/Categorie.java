
package controllers.BDDpackage;


import java.awt.*;

/**
 * Classe permettant de modéliser une Catégorie de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Categorie {
    /**
     * Nom de la catégorie
     */
    public String nom;
    /**
     * ID de la catégorie
     */
    public int id;
    /**
     * Couleur de la catégorie (non utilisé dans la version 1.0)
     */
    public Color couleur;

    Categorie() {

    }

    /** Constructeur
     * @param id        : id de la catégorie
     * @param nom       : nom de la catégorie
     * @param couleur   : Couleur de la catégorie (non utilisé dans la version 1.0)
     */
    public Categorie(int id, String nom, Color couleur){
        this.id = id;
        this.nom = nom;
        this.couleur = couleur;
    }

    /** Constructeur sans couleur
     * @param id    : id de la catégorie
     * @param nom   : nom de la catégorie
     */
    public Categorie(int id, String nom){
        this(id,nom,null);
    }

    /** Permet d'avoir le nom de la catégorie
     * @return le nom de la catégorie
     */
    public String getName()
    {
        return nom;
    }

}
