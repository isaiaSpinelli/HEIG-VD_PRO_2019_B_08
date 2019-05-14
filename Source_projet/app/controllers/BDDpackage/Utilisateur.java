
package controllers.BDDpackage;



import java.awt.*;
import java.util.ArrayList;

/**
 * Classe permettant de modéliser un Utilisateur de la base de donnée
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class Utilisateur {

    /**
     * ID de l'utilisateur
     */
    public int id;
    /**
     * prenom de l'utilisateur
     */
    public String prenom;
    /**
     * nom de l'utilisateur
     */
    public String nom;
    /**
     * email de l'utilisateur
     */
    public String email;
    /**
     * pseudo de l'utilisateur
     */
    public String pseudo;
    /**
     * genre de l'utilisateur
     */
    public String genre;/**
     * anniversaire de l'utilisateur
     */
    public String anniversaire;
    /**
     * cree_a de l'utilisateur
     */
    public String cree_a;
    /**
     * droit de l'utilisateur
     */
    public String droit;
    /**
     * statut l'utilisateur
     */
    public String statut;
    /**
     * pays de l'utilisateur
     */
    public String pays;
    /**
     * options de l'utilisateur
     */
    public ArrayList<Boolean> options;
    /**
     * solde courant de l'utilisateur
     */
    public double solde;

    /**
     * Constructeur vide
     */
    public Utilisateur() {
        this.id = 0;
    }

    /** Constructeur complet
     * @param id
     * @param prenom
     * @param nom
     * @param email
     * @param pseudo
     * @param genre
     * @param anniversaire
     * @param cree_a
     * @param droit
     * @param statut
     * @param pays
     * @param options
     * @param solde
     */
    public Utilisateur(int id, String prenom, String nom, String email, String pseudo, String genre, String anniversaire, String cree_a, String droit, String statut, String pays, ArrayList<Boolean> options,double solde) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.pseudo = pseudo;
        this.genre = genre;
        this.anniversaire = anniversaire;
        this.cree_a = cree_a;
        this.droit = droit;
        this.statut = statut;
        this.pays = pays;
        this.options = options;
        this.solde = solde;
    }

    /** Récupère l'id de l'utilisateur
     * @return l'Id de l'utilisateur
     */
    public int getId()
    {
        return this.id;
    }

    /** Récupère le solde courant de l'utilisateur
     * @return le solde courant de l'utilisateur
     */
    public double getSolde(){ return this.solde; }

    /** Récupère la liste des options des paramètres
     * @return la liste des options des paramètres
     */
    public ArrayList<Boolean> getOptions() {
        return options;
    }
}
