
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

    public int id;
    public String prenom;
    public String nom;
    public String email;
    public String pseudo;
    public String genre;
    public String anniversaire;
    public String cree_a;
    public String droit;
    public String statut;
    public String pays;
    public ArrayList<Boolean> options;
    public double solde;

    public Utilisateur() {
        this.id = 0;
    }

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

    public int getId()
    {
        return this.id;
    }

    public double getSolde(){ return this.solde; }

    public ArrayList<Boolean> getOptions() {
        return options;
    }
}
